package io.github.medianik.starter.telegram.annotation.param

/**
 * Resolves the param with whole text that was sent by user.
 *
 * If user inputs command `"/command param0 param1 param2"`,
 * the param will be resolved with `"/command param0 param1 param2"`
 */
@Target(AnnotationTarget.VALUE_PARAMETER)
@Retention(AnnotationRetention.RUNTIME)
@MustBeDocumented
annotation class WholeTextValue