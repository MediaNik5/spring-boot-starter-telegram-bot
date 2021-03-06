package io.github.medianik.starter.telegram.resolver

import dev.inmo.tgbotapi.extensions.api.send.reply
import dev.inmo.tgbotapi.extensions.behaviour_builder.BehaviourContext
import dev.inmo.tgbotapi.types.message.abstracts.CommonMessage
import dev.inmo.tgbotapi.types.message.content.MessageContent
import io.github.medianik.starter.telegram.annotation.NoReply
import io.github.medianik.starter.telegram.exception.NoReturnValueException
import io.github.medianik.starter.telegram.filter.CommandRequest
import io.github.medianik.starter.telegram.filter.CommandResponse
import io.github.medianik.starter.telegram.filter.FilterContext
import io.github.medianik.starter.telegram.filter.filters.CommandReturnValueFilter
import io.github.medianik.starter.telegram.util.hasAnnotationInherited
import io.github.medianik.starter.telegram.util.throwExceptionIfNotIgnored
import org.springframework.stereotype.Component
import kotlin.reflect.KFunction
import kotlin.reflect.KType

/**
 * Filter that transforms return String value into reply message to message sent by user.
 *
 * The return value is ignored if @[NoReply] annotation is present.
 */
@Component
class ReplyReturnValueResolver : CommandReturnValueFilter {
    override fun supportsReturnType(returnType: KType, function: KFunction<*>): Boolean {
        return returnType.classifier == String::class && !hasAnnotationInherited(function, NoReply::class)
    }

    override suspend fun processReturnValue(
        context: FilterContext,
        request: CommandRequest,
        response: CommandResponse,
        returnValue: Any?,
    ): Any? {
        returnValue as String?

        return if (returnValue != null) {
            context.bot.reply(request.incomingMessage, returnValue)
        } else {
            val function = context.command.function
            if(!function.returnType.isMarkedNullable) {
                throwExceptionIfNotIgnored(function) {
                    NoReturnValueException(function)
                }
            }
            return null
        }
    }
}