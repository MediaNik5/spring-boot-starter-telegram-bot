package io.github.medianik.starter.telegram.annotation.param

import com.soywiz.klock.DateTime
import java.time.*

/**
 * If param is annotated with @[ChatValue] annotation, it will be considered
 * as Message's send date.
 *
 * Supported date types are: [Instant], [LocalDate], [LocalTime], [LocalDateTime], [DateTime]
 */
@Target(AnnotationTarget.VALUE_PARAMETER)
@Retention(AnnotationRetention.RUNTIME)
@MustBeDocumented
annotation class SendDateValue
