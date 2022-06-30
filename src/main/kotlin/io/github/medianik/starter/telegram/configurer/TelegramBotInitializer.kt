package io.github.medianik.starter.telegram.configurer

import dev.inmo.tgbotapi.extensions.api.telegramBot
import dev.inmo.tgbotapi.extensions.behaviour_builder.BehaviourContext
import dev.inmo.tgbotapi.extensions.behaviour_builder.buildBehaviourWithLongPolling
import dev.inmo.tgbotapi.extensions.utils.updates.retrieving.setWebhookInfoAndStartListenWebhooks
import io.github.medianik.starter.telegram.config.TelegramBotProperties
import io.ktor.client.plugins.*
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.launch
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import java.util.concurrent.CancellationException
import java.util.concurrent.Executors
import javax.annotation.PostConstruct

private val logger = LoggerFactory.getLogger(TelegramBotInitializer::class.java)

@Component
class TelegramBotInitializer(
    private val telegramBotProperties: TelegramBotProperties,
    private val configurers: List<TelegramBotConfigurer>,
) {

    @OptIn(DelicateCoroutinesApi::class)
    @PostConstruct
    fun init() {
        val pool = Executors.newSingleThreadExecutor()
        val dispatcher = pool.asCoroutineDispatcher()
        GlobalScope.launch(dispatcher) {
            val bot = telegramBot(telegramBotProperties.botToken)
            val job = bot.buildBehaviourWithLongPolling(defaultExceptionsHandler = ::handleException) {
                configure()
            }
            logger.info("TelegramBot is running")
            job.join()
        }
    }

    private fun handleException(e: Throwable) {
        if (e is HttpRequestTimeoutException) // ignore no updates
            return
        if (e is CancellationException && e.cause == null) // ignore normal end of job
            return
        logger.error("Unhandled exception in TelegramBot", e)
    }

    private suspend fun BehaviourContext.configure() {
        for (configurer in configurers) {
            configurer.configure(this)
        }
    }
}