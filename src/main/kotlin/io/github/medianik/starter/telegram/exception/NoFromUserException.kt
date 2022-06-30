package io.github.medianik.starter.telegram.exception

import dev.inmo.tgbotapi.types.message.abstracts.CommonMessage
import dev.inmo.tgbotapi.types.message.content.MessageContent

class NoFromUserException(incomingMessage: CommonMessage<out MessageContent>) : BotCommandException() {
    override val message: String = "Incoming message had no from user: $incomingMessage"
}