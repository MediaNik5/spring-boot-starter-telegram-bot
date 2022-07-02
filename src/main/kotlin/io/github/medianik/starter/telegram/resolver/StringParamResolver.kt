package io.github.medianik.starter.telegram.resolver

import dev.inmo.tgbotapi.extensions.behaviour_builder.BehaviourContext
import dev.inmo.tgbotapi.extensions.utils.requireTextContent
import dev.inmo.tgbotapi.types.message.abstracts.CommonMessage
import dev.inmo.tgbotapi.types.message.content.MessageContent
import dev.inmo.tgbotapi.utils.PreviewFeature
import io.github.medianik.starter.telegram.annotation.param.Param
import io.github.medianik.starter.telegram.exception.NoSuchParameterException
import io.github.medianik.starter.telegram.filter.filters.CommandParameterFilter
import io.github.medianik.starter.telegram.util.hasAnnotationInherited
import io.github.medianik.starter.telegram.util.indexOfParameterWithAnnotation
import io.github.medianik.starter.telegram.util.throwExceptionIfNotIgnored
import org.springframework.stereotype.Component
import kotlin.reflect.KFunction
import kotlin.reflect.KParameter

/**
 * Filter finds the parameter with the [Param] annotation and return its value.
 *
 * If user inputs command `"/command param0 param1 param2"` and the signature of function is
 * ```
 * fun command(@Param param0: String, param1: SomeType, param2: SomeOtherType, @Param param3: String, @Param param4: String)
 * ```
 * and [parameter] is param3, then the function will return value of "param1" from the user input.
 */
@Component
class StringParamResolver : CommandParameterFilter {
    override fun supportsParameter(parameter: KParameter, function: KFunction<*>): Boolean {
        return String::class == parameter.type.classifier && hasAnnotationInherited(parameter, Param::class)
    }
    @OptIn(PreviewFeature::class)
    override suspend fun resolveParameter(
        bot: BehaviourContext,
        incomingMessage: CommonMessage<out MessageContent>,
        function: KFunction<*>,
        parameter: KParameter,
    ): String? {
        val index: Int = function.indexOfParameterWithAnnotation(parameter, Param::class)

        val text = incomingMessage.content.requireTextContent().text
        val parameters = text.split(" ")
        if (index + 1 < parameters.size) {
            return parameters[index + 1] // +1 for '/command'
        }

        if (!parameter.isOptional) {
            throwExceptionIfNotIgnored(function) {
                NoSuchParameterException(
                    parameter.name!!,
                    parameter.index,
                )
            }
        }
        return null
    }
}