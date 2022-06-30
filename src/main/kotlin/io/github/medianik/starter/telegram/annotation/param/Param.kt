package io.github.medianik.starter.telegram.annotation.param


/**
 * Marks the parameter as user input param.
 *
 * If user inputs command `"/command param0 param1 param2"` and the signature of function is
 * ```
 * fun command(@Param param0: String, param1: SomeType, param2: SomeOtherType, @Param param3: String, @Param param4: String)
 * ```
 * param0, param3 and param4 will be filled with appropriate values "param0", "param1" and "param2" respectively.
 */
@Target(AnnotationTarget.VALUE_PARAMETER)
@Retention(AnnotationRetention.RUNTIME)
@MustBeDocumented
annotation class Param
