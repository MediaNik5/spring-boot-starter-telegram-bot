package io.github.medianik.starter.telegram.processor

import kotlin.reflect.KFunction
import kotlin.reflect.KParameter

interface BotCommand {
    val command: String
    val description: String
    val example: String
    val function: KFunction<*>
    suspend fun execute(args: Map<KParameter, Any?>): Any?
}