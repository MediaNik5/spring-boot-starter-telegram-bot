package io.github.medianik.starter.telegram.filter

import org.springframework.core.annotation.AnnotationUtils
import org.springframework.core.annotation.Order

class FilterComparator private constructor() : Comparator<CommandFilter> {
    override fun compare(first: CommandFilter, second: CommandFilter): Int {

        val firstOrderAnnotation = AnnotationUtils.findAnnotation(first::class.java, Order::class.java)
        val secondOrderAnnotation = AnnotationUtils.findAnnotation(second::class.java, Order::class.java)
        if (firstOrderAnnotation == null && secondOrderAnnotation == null)
            return 0
        if (firstOrderAnnotation == null)
            return 1
        if (secondOrderAnnotation == null)
            return -1
        return 0
    }

    companion object {
        val instance = FilterComparator()
    }
}