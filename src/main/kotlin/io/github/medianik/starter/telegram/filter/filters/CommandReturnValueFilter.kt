package io.github.medianik.starter.telegram.filter.filters

import dev.inmo.tgbotapi.extensions.behaviour_builder.BehaviourContext
import dev.inmo.tgbotapi.types.message.abstracts.CommonMessage
import dev.inmo.tgbotapi.types.message.content.MessageContent
import io.github.medianik.starter.telegram.annotation.FilterAfter
import io.github.medianik.starter.telegram.filter.CommandFilter
import io.github.medianik.starter.telegram.filter.CommandRequest
import io.github.medianik.starter.telegram.filter.CommandResponse
import io.github.medianik.starter.telegram.filter.FilterContext
import kotlin.reflect.KFunction
import kotlin.reflect.KType

@FilterAfter(CommandLauncherFilter::class, 100u)
interface CommandReturnValueFilter : CommandFilter {
    override fun isApplicable(function: KFunction<*>): Boolean {
        return supportsReturnType(function.returnType, function)
    }

    fun supportsReturnType(returnType: KType, function: KFunction<*>): Boolean

    override suspend fun filter(context: FilterContext, request: CommandRequest, response: CommandResponse) {
        response.result =
            processReturnValue(context, request, response, response.result)
    }

    suspend fun processReturnValue(
        context: FilterContext,
        request: CommandRequest,
        response: CommandResponse,
        returnValue: Any?,
    ): Any?
}
