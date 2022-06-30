package io.github.medianik.starter.telegram.annotation.param

import dev.inmo.tgbotapi.types.message.abstracts.CommonMessage

/**
 * If param is annotated with @[MessageValue] annotation,
 * it will be considered as Message reference.
 *
 * If param is of type [Long], it will be resolved as Message id.
 *
 * If param is of type(or subtype of) [CommonMessage], it will be resolved as Message.
 *
 * If param is of type(or subtype of) [CommonMessage], it doesn't
 * need to be annotated with @[MessageValue] annotation, it will be resolved automatically.
 */
@Target(AnnotationTarget.VALUE_PARAMETER)
@Retention(AnnotationRetention.RUNTIME)
@MustBeDocumented
annotation class MessageValue
