package io.github.medianik.starter.telegram.filter

import io.github.medianik.starter.telegram.annotation.FilterAfter
import io.github.medianik.starter.telegram.annotation.FilterBefore
import org.slf4j.LoggerFactory
import org.springframework.core.annotation.AnnotationUtils
import org.springframework.core.annotation.Order
import kotlin.reflect.KClass

private val logger = LoggerFactory.getLogger(CommandFilterChain::class.java)

interface CommandFilterChain {
    fun addBefore(filter: CommandFilter, before: KClass<out CommandFilter>, offset: Int = 1)
    fun addAfter(filter: CommandFilter, before: KClass<out CommandFilter>, offset: Int = 1)
    fun addWithOrder(filter: CommandFilter, order: Int)

    suspend fun filter(
        context: FilterContext,
        request: CommandRequest,
        response: CommandResponse = CommandResponse.empty(),
    ): Any?

    companion object {
        fun of(
            filters: Iterable<CommandFilter>,
        ): CommandFilterChain {
            val chain = ChainImpl()
            val previousNotFoundFilters = hashSetOf<CommandFilter>()
            for (filter in filters) {
                val clazz = filter::class

                val order = AnnotationUtils.findAnnotation(clazz.java, Order::class.java)?.value
                if (order != null) {
                    logger.trace("Adding filter {} with order {}", clazz.simpleName, order)
                    chain.addWithOrder(filter, order)
                    continue
                }

                if (!addToChainBeforeOrAfter(clazz, chain, filter, previousNotFoundFilters)) {
                    logger.warn("Adding filter {} with order 0, because there is no specified order for this filter", clazz.simpleName)
                    chain.addWithOrder(filter, 0)
                }
            }

            val currentNotFoundFilters = hashSetOf<CommandFilter>()

            while (previousNotFoundFilters.isNotEmpty() && previousNotFoundFilters != currentNotFoundFilters) {
                currentNotFoundFilters.clear()
                for (filter in previousNotFoundFilters) {
                    val clazz = filter::class

                    addToChainBeforeOrAfter(clazz, chain, filter, currentNotFoundFilters, previousNotFoundFilters)
                }
            }

            if (currentNotFoundFilters.isNotEmpty()) {
                throw IllegalStateException("Not all filters were properly ordered. " +
                        "The error is probably that filters below are annotated with @FilterAfter or @FilterBefore, " +
                        "but filters specified in those annotations are not spring beans, so they could not be found. " +
                        "${currentNotFoundFilters.map { it::class.qualifiedName }}."
                )
            }

            chain.postProccess()

            logger.info("CommandFilterChain created with {} filters.", chain.filters.size)
            logger.trace("${chain.filters.map { it::class.qualifiedName }}")

            return chain
        }

        /**
         * Determines whether filter should be added before or after another filter.
         * If the order is not specified, it does nothing and returns false
         *
         * If the order is specified, it adds the filter to the chain and returns true.
         *
         * @return true if [filter] has determined order.
         * it could have been or have been not added to chain.
         */
        private fun addToChainBeforeOrAfter(
            clazz: KClass<out CommandFilter>,
            chain: CommandFilterChain,
            filter: CommandFilter,
            currentNotFoundFilters: MutableSet<CommandFilter>,
            previousNotFoundFilters: MutableSet<CommandFilter>? = null,
        ): Boolean {
            if (addBefore(clazz, chain, filter, previousNotFoundFilters, currentNotFoundFilters))
                return true

            if (addAfter(clazz, chain, filter, previousNotFoundFilters, currentNotFoundFilters))
                return true
            return false
        }

        /**
         * Determines whether filter should be added before another filter.
         * If the order is not specified, it does nothing and returns false
         *
         * If the order is specified, it attempts to add the filter to the chain
         * and returns true.
         *
         * It might not be added to chain, because the dependent filter could not be found in chain yet.
         *
         * @return true if [filter] has determined order.
         */
        private fun addBefore(
            clazz: KClass<out CommandFilter>,
            chain: CommandFilterChain,
            filter: CommandFilter,
            previousNotFoundFilters: MutableSet<CommandFilter>?,
            currentNotFoundFilters: MutableSet<CommandFilter>,
        ): Boolean {
            val filterBeforeAnnotation = AnnotationUtils.findAnnotation(clazz.java, FilterBefore::class.java)
            if (filterBeforeAnnotation != null) {
                try {
                    chain.addBefore(filter, filterBeforeAnnotation.value, filterBeforeAnnotation.offset.toInt())
                    previousNotFoundFilters?.remove(filter)
                } catch (e: IllegalArgumentException) {
                    currentNotFoundFilters.add(filter)
                }
                return true
            }
            return false
        }


        /**
         * Determines whether filter should be added after another filter.
         * If the order is not specified, it does nothing and returns false
         *
         * If the order is specified, it attempts to add the filter to the chain
         * and returns true.
         *
         * It might not be added to chain, because the dependent filter could not be found in chain yet.
         *
         * @return true if [filter] has determined order.
         */
        private fun addAfter(
            clazz: KClass<out CommandFilter>,
            chain: CommandFilterChain,
            filter: CommandFilter,
            previousNotFoundFilters: MutableSet<CommandFilter>?,
            currentNotFoundFilters: MutableSet<CommandFilter>,
        ): Boolean {
            val filterAfterAnnotation = AnnotationUtils.findAnnotation(clazz.java, FilterAfter::class.java)
            if (filterAfterAnnotation != null) {
                try {
                    chain.addAfter(filter, filterAfterAnnotation.value, filterAfterAnnotation.offset.toInt())
                    previousNotFoundFilters?.remove(filter)
                } catch (e: IllegalArgumentException) {
                    currentNotFoundFilters.add(filter)
                }
                return true
            }
            return false
        }
    }

    fun postProccess()
}

private class ChainImpl : CommandFilterChain {
    val filters = mutableListOf<OrderedBotCommandFilter>()
    val filterOrders = ClassOrderRegistration<CommandFilter>()

    override fun addWithOrder(filter: CommandFilter, order: Int) {
        filterOrders.put(filter::class, order)
        filters.add(OrderedBotCommandFilter(filter, order))
    }

    override fun addBefore(filter: CommandFilter, before: KClass<out CommandFilter>, offset: Int) {
        addFilterAtOffsetOf(filter, before, -offset)
    }

    override fun addAfter(filter: CommandFilter, before: KClass<out CommandFilter>, offset: Int) {
        addFilterAtOffsetOf(filter, before, offset)
    }

    private fun addFilterAtOffsetOf(filter: CommandFilter, before: KClass<out CommandFilter>, offset: Int) {
        val order = filterOrders.getOrder(before) ?: throw IllegalArgumentException("Filter $before not registered")
        val newOrder = order + offset
        filterOrders.put(filter::class, newOrder)
        filters.add(OrderedBotCommandFilter(filter, newOrder))
    }

    override suspend fun filter(
        context: FilterContext,
        request: CommandRequest,
        response: CommandResponse,
    ): Any? {
        val filters = filters.map { it.filter }
        for (filter in filters) {
            if (filter.isApplicable(context.command.function))
                filter.filter(context, request, response)
        }

        return response.result
    }

    override fun postProccess() {
        filters.sortBy { it.order }
    }
}

private data class OrderedBotCommandFilter(
    val filter: CommandFilter,
    val order: Int,
)