package io.github.medianik.starter.telegram.exception

import io.github.medianik.starter.telegram.util.qualifiedName
import kotlin.reflect.KFunction
import kotlin.reflect.KType

class NoProcessorForReturnType(private val type: KType, private val functionName: String) : BotCommandException() {
    override val message: String
        get() = "No processor found for return type $type for function $functionName. " +
                "To use this type, you need to add a processor for it " +
                "or disable this inspection by annotating your method @IgnoreException(NoProcessorForReturnType::class)."

    companion object {
        fun create(type: KType, function: KFunction<*>): NoProcessorForReturnType {
            return NoProcessorForReturnType(type, function.qualifiedName)
        }
    }
}