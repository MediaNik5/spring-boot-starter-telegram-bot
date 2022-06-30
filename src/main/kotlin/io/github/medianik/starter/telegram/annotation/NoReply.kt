package io.github.medianik.starter.telegram.annotation

/**
 * If method annotated with @[BotCommand] has return value,
 * and this annotation is present on the method, then return value will be ignored
 * and no replies will be sent to user on command.
 */
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
@MustBeDocumented
annotation class NoReply
