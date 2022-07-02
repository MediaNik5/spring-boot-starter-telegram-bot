package io.github.medianik.starter.telegram.resolver

import dev.inmo.tgbotapi.extensions.behaviour_builder.BehaviourContext
import dev.inmo.tgbotapi.extensions.utils.requireTextContent
import dev.inmo.tgbotapi.types.message.abstracts.CommonMessage
import dev.inmo.tgbotapi.types.message.content.MessageContent
import dev.inmo.tgbotapi.utils.PreviewFeature
import io.github.medianik.starter.telegram.annotation.param.Param
import io.github.medianik.starter.telegram.annotation.param.RemainingParams
import io.github.medianik.starter.telegram.filter.filters.CommandParameterFilter
import io.github.medianik.starter.telegram.util.hasAnnotationInherited
import io.github.medianik.starter.telegram.util.indexOfParameterWithAnnotation
import org.springframework.stereotype.Component
import kotlin.reflect.KFunction
import kotlin.reflect.KParameter


/**
 * Filter that resolves parameter with all the remaining parameters and returns its value.
 *
 * If user inputs command `"/command param0 param1 param2"` and the signature of function is
 * ```
 * fun command(@Param param0: String, param1: SomeType, param2: SomeOtherType, @RemainingParams param3: String, @Param param4: String)
 * ```
 * and [parameter] is param3, then the function will return value of both "param1" and "param2" from the user input as single string: "param1 param2".
 */
@Component
class RemainingParametersResolver : CommandParameterFilter {
    override fun supportsParameter(parameter: KParameter, function: KFunction<*>): Boolean {
        return function.returnType.classifier == String::class && hasAnnotationInherited(function, RemainingParams::class)
    }

    @OptIn(PreviewFeature::class)
    override suspend fun resolveParameter(
        bot: BehaviourContext,
        incomingMessage: CommonMessage<out MessageContent>,
        function: KFunction<*>,
        parameter: KParameter,
    ): Any? {
        val index = function.indexOfParameterWithAnnotation(parameter, Param::class)

        val text = incomingMessage.content.requireTextContent().text
        val parameters = text.split(" ").drop(index + 1)
        return parameters.joinToString(" ")
    }
}