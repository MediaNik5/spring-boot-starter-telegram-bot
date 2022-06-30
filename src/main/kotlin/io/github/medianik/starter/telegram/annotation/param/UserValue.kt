package io.github.medianik.starter.telegram.annotation.param

import dev.inmo.tgbotapi.types.chat.User

/**
 * If param is annotated with @[UserValue] annotation,
 * it will be considered as User reference.
 *
 * If param is of type [Long], it will be considered as User id.
 *
 * If param is of type(or subtype of) [User], it will be considered as User instance.
 *
 * If param is of type(or subtype of) [User], it doesn't
 * need to be annotated with @[UserValue] annotation, it will be resolved automatically.
 */
@Target(AnnotationTarget.VALUE_PARAMETER)
@Retention(AnnotationRetention.RUNTIME)
@MustBeDocumented
annotation class UserValue
