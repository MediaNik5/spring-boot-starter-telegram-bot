package io.github.medianik.starter.telegram.filter

import dev.inmo.tgbotapi.extensions.behaviour_builder.BehaviourContext
import dev.inmo.tgbotapi.types.message.abstracts.CommonMessage
import dev.inmo.tgbotapi.types.message.content.MessageContent
import io.github.medianik.starter.telegram.processor.BotCommand
import kotlin.reflect.KFunction

data class FilterContext(
    val bot: BehaviourContext,
    val command: BotCommand,
)

data class CommandRequest(
    val incomingMessage: CommonMessage<out MessageContent>,
    val parameterSize: Int = 0,
    val parameters: Array<Any?> = arrayOfNulls(parameterSize),
)

data class CommandResponse(
    var result: Any?,
) {
    companion object {
        fun empty() = CommandResponse(null)
    }
}

interface CommandFilter {
    fun isApplicable(function: KFunction<*>): Boolean

    suspend fun filter(
        context: FilterContext,
        request: CommandRequest,
        response: CommandResponse,
    )
}