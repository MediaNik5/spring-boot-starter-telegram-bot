package io.github.medianik.starter.telegram.processor

import kotlin.reflect.KFunction
import kotlin.reflect.KParameter

class SimpleBotCommand(
    override val command: String,
    override val description: String,
    override val example: String,
    override val function: KFunction<*>,
    private val handler: suspend (Map<KParameter, Any?>) -> Any?,
) : BotCommand {

    override suspend fun execute(args: Map<KParameter, Any?>): Any? {
        return handler(args)
    }
}