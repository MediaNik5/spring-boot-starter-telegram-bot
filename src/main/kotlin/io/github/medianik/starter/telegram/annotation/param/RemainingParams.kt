package io.github.medianik.starter.telegram.annotation.param


/**
 * Marks the parameter as left user input params.
 *
 * If user inputs command `"/command param0 param1 param2"` and the signature of function is
 * ```
 * fun command(@Param param0: String, param1: SomeType, param2: SomeOtherType, @RemainingParams param3: String, @Param param4: String)
 * ```
 * then param3 will be filled with values of both "param1" and "param2" from the user input as single string: "param1 param2".
 */
@Target(AnnotationTarget.VALUE_PARAMETER)
@Retention(AnnotationRetention.RUNTIME)
@MustBeDocumented
annotation class RemainingParams
