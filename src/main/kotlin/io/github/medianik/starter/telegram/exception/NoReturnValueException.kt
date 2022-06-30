package io.github.medianik.starter.telegram.exception

import kotlin.reflect.KFunction

class NoReturnValueException(private val function: KFunction<*>) : BotExecutionException() {
}