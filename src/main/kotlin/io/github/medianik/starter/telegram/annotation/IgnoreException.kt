package io.github.medianik.starter.telegram.annotation

import io.github.medianik.starter.telegram.exception.BotCommandException
import kotlin.reflect.KClass

/**
 * If exception of type [value] is thrown while executing command or forming [CommandFilterChain],
 * it will be dismissed if possible and execution will continue.
 *
 * The appropriate warning will be logged, if [printWarning] is true
 */
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
@MustBeDocumented
@Repeatable
annotation class IgnoreException(
    val value: KClass<out BotCommandException>,
    val printWarning: Boolean = true,
)
