//package io.github.medianik.starter.telegram.annotation
//
//import io.github.medianik.starter.telegram.filter.CommandFilter
//import kotlin.reflect.KClass
//
//
///**
// * Marks the filter as one that must happen after filter [value].
// * [offset] is optional to mark the "distance" between the two filters.
// *
// * [offset] is useful when you have a lot of filters and you want to ensure they are sequential one after another.
// *
// * If [value] filter had order `n`, then filter marked with this annotation will have order `n + offset`.
// */
//@Target(AnnotationTarget.CLASS, AnnotationTarget.TYPE)
//@Retention(AnnotationRetention.RUNTIME)
//@MustBeDocumented
//annotation class FilterAfter(
//    val value: KClass<out CommandFilter>,
//    val offset: UInt = 1u,
//)
