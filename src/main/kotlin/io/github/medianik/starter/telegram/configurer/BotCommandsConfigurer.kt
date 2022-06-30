package io.github.medianik.starter.telegram.configurer

import dev.inmo.tgbotapi.extensions.behaviour_builder.BehaviourContext
import dev.inmo.tgbotapi.extensions.behaviour_builder.triggers_handling.onCommand
import io.github.medianik.starter.telegram.exception.BotCommandException
import io.github.medianik.starter.telegram.exception.BotExecutionException
import io.github.medianik.starter.telegram.exception.handler.CommandExceptionHandler
import io.github.medianik.starter.telegram.exception.handler.ExecutionExceptionHandler
import io.github.medianik.starter.telegram.filter.CommandRequest
import io.github.medianik.starter.telegram.filter.CommandResponse
import io.github.medianik.starter.telegram.filter.FilterContext
import io.github.medianik.starter.telegram.processor.BotCommand
import io.github.medianik.starter.telegram.processor.BotCommandsAggregator
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import kotlin.reflect.full.valueParameters
import kotlin.system.measureTimeMillis

private val logger = LoggerFactory.getLogger(BotCommandsConfigurer::class.java)

@Component
class BotCommandsConfigurer(
    private val botCommandsAggregator: BotCommandsAggregator,
    private val executionExceptionHandlers: List<ExecutionExceptionHandler>,
    private val commandExceptionHandlers: List<CommandExceptionHandler>
) : TelegramBotConfigurer {
    override suspend fun configure(bot: BehaviourContext) {
        for (botCommand in botCommandsAggregator.commands.values) {
            logger.trace("Configuring command: {}", botCommand.command)
            bot.onCommand(botCommand.command, requireOnlyCommandInMessage = false) { message ->
                val context = FilterContext(bot, botCommand)
                val request = CommandRequest(message, botCommand.function.valueParameters.size)
                val response = CommandResponse.empty()
                try {
                    val time = measureTimeMillis {
                        botCommandsAggregator.globalFilterChain.filter(context, request, response)
                    }
                    logger.trace("Command ${botCommand.command} executed for $time ms")
                } catch (e: BotExecutionException) {
                    handleExecutionException(e, context, request, response, botCommand)
                } catch (e: BotCommandException) {
                    handleCommandException(e, context, request, response, botCommand)
                } catch (e: Throwable) {
                    logUnhandledException(botCommand, e)
                }
            }
        }
        logger.info("Configured ${botCommandsAggregator.commands.size} commands: ${botCommandsAggregator.commands.keys.joinToString(", ")}")
    }

    private suspend fun handleCommandException(
        e: BotCommandException,
        context: FilterContext,
        request: CommandRequest,
        response: CommandResponse,
        botCommand: BotCommand,
    ) {
        for (handler in commandExceptionHandlers) {
            if (handler.handle(e, context, request, response)) {
                return
            }
        }
        logUnhandledException(botCommand, e)
    }

    private suspend fun handleExecutionException(
        e: BotExecutionException,
        context: FilterContext,
        request: CommandRequest,
        response: CommandResponse,
        botCommand: BotCommand,
    ) {
        for (handler in executionExceptionHandlers) {
            if (handler.handle(e, context, request, response)) {
                return
            }
        }
        logUnhandledException(botCommand, e)
    }

    private fun logUnhandledException(
        botCommand: BotCommand,
        e: Throwable,
    ) {
        logger.error(
            "Unhandled exception in TelegramBot while handling command " +
                    "${botCommand.command}, ignoring", e
        )
    }

}