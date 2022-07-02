package io.github.medianik.starter.telegram.filter.filters

import io.github.medianik.starter.telegram.exception.ParameterResolverNotFoundException
import io.github.medianik.starter.telegram.filter.CommandFilter
import io.github.medianik.starter.telegram.filter.CommandRequest
import io.github.medianik.starter.telegram.filter.CommandResponse
import io.github.medianik.starter.telegram.filter.FilterContext
import io.github.medianik.starter.telegram.util.throwExceptionIfNotIgnored
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Component
import kotlin.reflect.KFunction
import kotlin.reflect.KParameter
import kotlin.reflect.full.valueParameters

@Component
@Order(100)
class CommandLauncherFilter : CommandFilter {
    override fun isApplicable(function: KFunction<*>): Boolean = true

    override suspend fun filter(
        context: FilterContext,
        request: CommandRequest,
        response: CommandResponse,
    ) {
        val function = context.command.function
        val parameters = function.valueParameters

        val parametersMap = hashMapOf<KParameter, Any?>()
        parameters.forEachIndexed { i, parameter ->
            val value = request.parameters[i]
            if(value == null){
                if(!parameter.isOptional && parameter.type.isMarkedNullable){
                    throwExceptionIfNotIgnored(function) {
                        ParameterResolverNotFoundException(function, parameter)
                    }
                }
            } else {
                parametersMap[parameter] = value
            }
        }
        response.result = context.command.execute(parametersMap)
    }
}