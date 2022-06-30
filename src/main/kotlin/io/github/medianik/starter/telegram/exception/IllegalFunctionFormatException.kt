package io.github.medianik.starter.telegram.exception

import io.github.medianik.starter.telegram.util.qualifiedName
import kotlin.reflect.KFunction

class IllegalFunctionFormatException(private val function: String) : BotCommandException() {
    override val message: String
        get() = "Function $function is not allowed to be external or infix"

    companion object {
        fun create(function: KFunction<*>): IllegalFunctionFormatException {
            return IllegalFunctionFormatException(function.qualifiedName)
        }
    }
}