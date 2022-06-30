package io.github.medianik.starter.telegram.filter

import kotlin.reflect.KClass


class ClassOrderRegistration<T : Any> {
    private val clazzToOrder = hashMapOf<String, Int>()

    fun put(clazz: KClass<out T>, order: Int) {
        val className = clazz.simpleName ?: throw IllegalArgumentException("Class must have name")
        if (clazzToOrder.containsKey(className)) {
            throw IllegalArgumentException("Class $className already registered")
        }
        clazzToOrder[className] = order
    }

    fun getOrder(clazz: KClass<out T>): Int? {
        var outClazz: Class<*>? = clazz.java
        while (outClazz != null) {
            val order = clazzToOrder[outClazz.simpleName ?: throw IllegalArgumentException("Class must have name")]
            if (order != null) {
                return order
            }
            outClazz = outClazz.superclass
        }
        return null
    }
}