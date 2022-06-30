package io.github.medianik.starter.telegram.exception

import io.github.medianik.starter.telegram.util.qualifiedName
import kotlin.reflect.KFunction
import kotlin.reflect.KParameter

class DuplicateParameterFilterException(function: KFunction<*>, parameter: KParameter) : BotCommandException() {
    override val message =
        "Duplicate parameter filter for parameter ${parameter.name} for function ${function.qualifiedName}"
}