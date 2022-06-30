package io.github.medianik.starter.telegram.exception

import kotlin.reflect.KClass
import kotlin.reflect.KFunction
import kotlin.reflect.KParameter

class InvalidTypeException(function: KFunction<*>, foundParam: KParameter, expectedTypes: Iterable<KClass<*>>) : BotCommandException() {
    override val message: String =
        "Type of param ${foundParam.name} in function $function is not one of expected types: ${expectedTypes.map { it.simpleName }.joinToString(", ")}"
}