package io.github.medianik.starter.telegram.configurer

import dev.inmo.tgbotapi.extensions.behaviour_builder.BehaviourContext

interface TelegramBotConfigurer {
    suspend fun configure(bot: BehaviourContext)
}