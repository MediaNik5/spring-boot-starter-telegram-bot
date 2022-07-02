package io.github.medianik.starter.telegram.filter.filters

import dev.inmo.tgbotapi.extensions.behaviour_builder.BehaviourContext
import dev.inmo.tgbotapi.types.message.abstracts.CommonMessage
import dev.inmo.tgbotapi.types.message.content.MessageContent
import io.github.medianik.starter.telegram.annotation.FilterBefore
import io.github.medianik.starter.telegram.exception.DuplicateParameterFilterException
import io.github.medianik.starter.telegram.filter.CommandFilter
import io.github.medianik.starter.telegram.filter.CommandRequest
import io.github.medianik.starter.telegram.filter.CommandResponse
import io.github.medianik.starter.telegram.filter.FilterContext
import io.github.medianik.starter.telegram.util.throwExceptionIfNotIgnored
import kotlin.reflect.KFunction
import kotlin.reflect.KParameter
import kotlin.reflect.full.valueParameters

@FilterBefore(CommandLauncherFilter::class, 100u)
interface CommandParameterFilter : CommandFilter {
    override fun isApplicable(function: KFunction<*>): Boolean {
        for (parameter in function.valueParameters) {
            if (supportsParameter(parameter, function)) {
                return true
            }
        }
        return false
    }

    override suspend fun filter(context: FilterContext, request: CommandRequest, response: CommandResponse) {
        val function = context.command.function
        for (parameter in function.valueParameters) {
            if (supportsParameter(parameter, function)) {
                if (request.parameters[parameter.index - 1] != null)
                    throwExceptionIfNotIgnored(function) { DuplicateParameterFilterException(function, parameter) }

                request.parameters[parameter.index - 1] =
                    resolveParameter(context, request, response, parameter)
            }
        }
    }

    fun supportsParameter(parameter: KParameter, function: KFunction<*>): Boolean

    suspend fun resolveParameter(
        context: FilterContext,
        request: CommandRequest,
        response: CommandResponse,
        parameter: KParameter,
    ): Any?
}