package io.github.medianik.starter.telegram.exception.handler

import io.github.medianik.starter.telegram.exception.BotCommandException
import io.github.medianik.starter.telegram.filter.CommandRequest
import io.github.medianik.starter.telegram.filter.CommandResponse
import io.github.medianik.starter.telegram.filter.FilterContext

/**
 * Handles BotCommandException
 */
interface CommandExceptionHandler {
    /**
     * Handles BotCommandException with context that was in filters chain
     *
     * @return true if exception was handled, false if exception should be handled by next handler
     */
    suspend fun handle(
        exception: BotCommandException,
        context: FilterContext,
        request: CommandRequest,
        response: CommandResponse,
    ): Boolean
}