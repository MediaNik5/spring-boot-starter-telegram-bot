package io.github.medianik.starter.telegram

import io.github.medianik.starter.telegram.config.TelegramBotProperties
import org.springframework.boot.autoconfigure.AutoConfigurationExcludeFilter
import org.springframework.boot.context.TypeExcludeFilter
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.FilterType

@Configuration
@EnableConfigurationProperties(TelegramBotProperties::class)
@ComponentScan(
    excludeFilters = [ComponentScan.Filter(
        type = FilterType.CUSTOM,
        classes = [TypeExcludeFilter::class]
    ), ComponentScan.Filter(type = FilterType.CUSTOM, classes = [AutoConfigurationExcludeFilter::class])]
)
class TelegramBotAutoConfiguration