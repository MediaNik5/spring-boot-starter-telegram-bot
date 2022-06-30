package io.github.medianik.starter.telegram.util

import io.github.medianik.starter.telegram.annotation.IgnoreException
import io.github.medianik.starter.telegram.exception.BotCommandException
import org.slf4j.LoggerFactory
import org.springframework.core.annotation.AnnotationUtils
import kotlin.reflect.KAnnotatedElement
import kotlin.reflect.KClass
import kotlin.reflect.KFunction
import kotlin.reflect.KParameter
import kotlin.reflect.full.valueParameters
import kotlin.reflect.jvm.javaMethod

val logger = LoggerFactory.getLogger("Util")

val KFunction<*>.qualifiedName: String
    get() = javaMethod?.declaringClass?.canonicalName + "#" + name + "(" + valueParameters.map { it.type.toString() + " " + it.name } + ")"

fun String.removeFirstSlashIfPresent(): String {
    return if (startsWith("/")) {
        substring(1)
    } else this
}

inline fun <reified T> throwExceptionIfNotIgnored(
    function: KFunction<*>,
    exceptionSupplier: () -> T,
) where T : BotCommandException {
    if (!ignoreException(function, T::class))
        throw exceptionSupplier()
}

@Suppress("NOTHING_TO_INLINE")
inline fun ignoreException(function: KFunction<*>, clazz: KClass<out BotCommandException>): Boolean {
    for (ignore in function.annotations.filterIsInstance<IgnoreException>()) {
        if (ignore.value != clazz)
            continue

        if (ignore.printWarning) {
            logger.warn("Exception ${clazz.qualifiedName} is ignored for function ${function.qualifiedName}")
        }
        return true
    }

    return false
}

fun <T> getAnnotationInherited(
    annotatedElement: KAnnotatedElement,
    searchAnnotation: KClass<T>,
): T? where T : Annotation {
    for (annotation in annotatedElement.annotations) {
        if (annotation.annotationClass == searchAnnotation) {
            return annotation as T
        }
    }
    for (annotation in annotatedElement.annotations) {
        val annotationInherited = AnnotationUtils.findAnnotation(annotation::class.java, searchAnnotation.java)
        if (annotationInherited != null) {
            return annotationInherited
        }
    }
    return null
}

fun <T> hasAnnotationInherited(
    annotatedElement: KAnnotatedElement,
    annotation: KClass<T>,
): Boolean where T : Annotation {
    return getAnnotationInherited(annotatedElement, annotation) != null
}

fun <R> KFunction<R>.indexOfParameterWithAnnotation(
    parameter: KParameter,
    annotation: KClass<out Annotation>,
): Int {
    var index = 0
    for (param in valueParameters) {
        if (param == parameter) {
            return index
        }
        if (hasAnnotationInherited(param, annotation)) {
            index++
        }
    }
    return -1
}

val KParameter.clazz get() = type.classifier as KClass<*>
val KParameter.clazzOrNull get() = type.classifier as? KClass<*>