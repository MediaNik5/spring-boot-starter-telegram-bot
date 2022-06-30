package io.github.medianik.starter.telegram.exception

import io.github.medianik.starter.telegram.util.qualifiedName
import kotlin.reflect.KFunction
import kotlin.reflect.KParameter

class ParameterResolverNotFoundException(function: KFunction<*>, parameter: KParameter) : BotCommandException() {
    override val message: String =
        "Could not find resolver for parameter ${parameter.name} in function ${function.qualifiedName} "
}