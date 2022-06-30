package io.github.medianik.starter.telegram.annotation.param

import dev.inmo.tgbotapi.extensions.behaviour_builder.BehaviourContext
import dev.inmo.tgbotapi.bot.TelegramBot

/**
 * If param is annotated with @[BotValue] annotation, it will be
 * considered as [TelegramBot] reference.
 *
 * It is used as a marker and doesn't affect anything.
 *
 * If your param has type [TelegramBot] or [BehaviourContext] it will be
 * automatically injected.
 */
@Target(AnnotationTarget.VALUE_PARAMETER)
@Retention(AnnotationRetention.SOURCE)
@MustBeDocumented
annotation class BotValue
