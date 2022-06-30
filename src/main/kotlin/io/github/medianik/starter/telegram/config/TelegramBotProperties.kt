package io.github.medianik.starter.telegram.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding

@ConstructorBinding
@ConfigurationProperties(prefix = "io.github.medianik.telegram")
class TelegramBotProperties(
    val botToken: String,
    val botUsername: String,
)