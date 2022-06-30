package io.github.medianik.starter.telegram.processor

import io.github.medianik.starter.telegram.BotHandlerHolder
import io.github.medianik.starter.telegram.annotation.component1
import io.github.medianik.starter.telegram.annotation.component2
import io.github.medianik.starter.telegram.annotation.component3
import io.github.medianik.starter.telegram.exception.DuplicateBotCommandException
import io.github.medianik.starter.telegram.exception.IllegalFunctionFormatException
import io.github.medianik.starter.telegram.filter.CommandFilter
import io.github.medianik.starter.telegram.filter.CommandFilterChain
import io.github.medianik.starter.telegram.filter.FilterComparator
import io.github.medianik.starter.telegram.util.removeFirstSlashIfPresent
import io.github.medianik.starter.telegram.util.throwExceptionIfNotIgnored
import org.springframework.core.annotation.AnnotationUtils
import org.springframework.stereotype.Component
import kotlin.reflect.KClass
import kotlin.reflect.KFunction
import kotlin.reflect.full.callSuspend
import kotlin.reflect.full.callSuspendBy
import kotlin.reflect.full.functions
import kotlin.reflect.jvm.javaMethod

private typealias CommandAnnotation = io.github.medianik.starter.telegram.annotation.BotCommand

@Component
class BotCommandsAggregator(
    botHandlers: BotHandlerHolder,
    filters: MutableList<CommandFilter>,
) {

    private val commandMap = hashMapOf<String, BotCommand>()
    val commands: Map<String, BotCommand>
        get() = commandMap

    final val globalFilterChain: CommandFilterChain

    init {
        filters.sortWith(FilterComparator.instance)
        globalFilterChain = CommandFilterChain.of(filters)

        for (component in botHandlers.components) {
            val commands = postProcessBotBean(component, component::class)

            for (command in commands) {
                commandMap.merge(command.command, command) { _, new ->
                    throw DuplicateBotCommandException(new.command)
                }
            }
        }
    }

    private fun postProcessBotBean(bean: Any, clazz: KClass<out Any>): List<SimpleBotCommand> {
        val commands = clazz.functions.filter(::checkCorrectBotCommandFunction).map { function ->
            var (command, description, example) = AnnotationUtils.findAnnotation(
                function.javaMethod!!,
                CommandAnnotation::class.java
            )!!
            command = command.removeFirstSlashIfPresent()

            SimpleBotCommand(command, description, example, function) { parameters ->
                // 0th parameter is 'this' keyword
                function.callSuspendBy(parameters + (function.parameters[0] to bean))
            }
        }

        return commands
    }

    private fun checkCorrectBotCommandFunction(function: KFunction<*>): Boolean {
        if (AnnotationUtils.findAnnotation(function.javaMethod!!, CommandAnnotation::class.java) == null)
            return false

        if (function.isExternal || function.isInfix) { // external and infix functions are not supported
            throwExceptionIfNotIgnored(function) {
                IllegalFunctionFormatException.create(function)
            }
        }
        return true
    }
}