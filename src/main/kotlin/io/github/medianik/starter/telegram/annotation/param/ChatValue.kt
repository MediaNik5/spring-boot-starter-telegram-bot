package io.github.medianik.starter.telegram.annotation.param

import dev.inmo.tgbotapi.types.chat.Chat

/**
 * If param is annotated with @[ChatValue] annotation, it will be considered as Chat
 * reference.
 *
 * If param is of type [Long] it will be resolved as chat id.
 *
 * If param is of type(or subtype of) [Chat]
 * it will be resolved as appropriate chat instance.
 *
 * If param is of type(or subtype of) [Chat], it doesn't
 * need to be annotated with @[ChatValue] annotation, it will be resolved automatically.
 */
@Target(AnnotationTarget.VALUE_PARAMETER)
@Retention(AnnotationRetention.RUNTIME)
@MustBeDocumented
annotation class ChatValue