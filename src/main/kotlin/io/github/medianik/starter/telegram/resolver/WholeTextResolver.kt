package io.github.medianik.starter.telegram.resolver

import dev.inmo.tgbotapi.extensions.behaviour_builder.BehaviourContext
import dev.inmo.tgbotapi.extensions.utils.extensions.raw.text
import dev.inmo.tgbotapi.types.message.abstracts.CommonMessage
import dev.inmo.tgbotapi.types.message.content.MessageContent
import dev.inmo.tgbotapi.utils.RiskFeature
import io.github.medianik.starter.telegram.annotation.param.WholeTextValue
import io.github.medianik.starter.telegram.filter.CommandRequest
import io.github.medianik.starter.telegram.filter.CommandResponse
import io.github.medianik.starter.telegram.filter.FilterContext
import io.github.medianik.starter.telegram.filter.filters.CommandParameterFilter
import io.github.medianik.starter.telegram.util.hasAnnotationInherited
import org.springframework.stereotype.Component
import kotlin.reflect.KFunction
import kotlin.reflect.KParameter

@Component
class WholeTextResolver : CommandParameterFilter {
    override fun supportsParameter(parameter: KParameter, function: KFunction<*>): Boolean {
        return parameter.type.classifier == String::class && hasAnnotationInherited(parameter, WholeTextValue::class)
    }

    @OptIn(RiskFeature::class)
    override suspend fun resolveParameter(
        context: FilterContext,
        request: CommandRequest,
        response: CommandResponse,
        parameter: KParameter,
    ): String? {
        return request.incomingMessage.text
    }
}