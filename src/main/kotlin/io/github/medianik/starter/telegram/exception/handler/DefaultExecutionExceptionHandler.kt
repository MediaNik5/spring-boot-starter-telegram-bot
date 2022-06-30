package io.github.medianik.starter.telegram.exception.handler

import dev.inmo.tgbotapi.extensions.api.send.reply
import dev.inmo.tgbotapi.types.message.content.TextContent
import dev.inmo.tgbotapi.types.message.textsources.code
import dev.inmo.tgbotapi.types.message.textsources.regular
import io.github.medianik.starter.telegram.exception.BotExecutionException
import io.github.medianik.starter.telegram.filter.CommandRequest
import io.github.medianik.starter.telegram.filter.CommandResponse
import io.github.medianik.starter.telegram.filter.FilterContext
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.stereotype.Component

@Component
@ConditionalOnProperty(name = ["io.github.medianik.telegram.exception.handler.enabled"], havingValue = "true", matchIfMissing = true)
class DefaultExecutionExceptionHandler : ExecutionExceptionHandler {
    override suspend fun handle(
        exception: BotExecutionException,
        context: FilterContext,
        request: CommandRequest,
        response: CommandResponse,
    ): Boolean {
        if(context.command.example.isNotBlank()) {
            context.bot.reply(
                request.incomingMessage,
                listOf(
                    regular("Sorry, you used command incorrectly. \nCorrect usage example: \n\n"),
                    code(context.command.example)
                )
            )
            return true
        }
        return false
    }
}