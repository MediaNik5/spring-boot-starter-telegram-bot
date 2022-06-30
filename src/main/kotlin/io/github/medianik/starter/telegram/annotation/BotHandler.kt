package io.github.medianik.starter.telegram.annotation

import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Component

/**
 * Marks the class as a Telegram Bot command handler.
 *
 * This class has to be Spring boot bean either by annotating it @[Component] or its "subtypes"
 * or made from spring bean factory.
 *
 * All methods that are intended to be handlers of commands must be annotated with @[BotCommand]
 *
 * @see [BotCommand]
 */
@Target(
    AnnotationTarget.CLASS, AnnotationTarget.ANNOTATION_CLASS, AnnotationTarget.TYPE,
    AnnotationTarget.VALUE_PARAMETER
)
@Retention(AnnotationRetention.RUNTIME)
@MustBeDocumented
@Qualifier
annotation class BotHandler
