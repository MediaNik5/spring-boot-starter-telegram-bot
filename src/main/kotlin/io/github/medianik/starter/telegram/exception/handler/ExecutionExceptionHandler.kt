package io.github.medianik.starter.telegram.exception.handler

import io.github.medianik.starter.telegram.exception.BotExecutionException
import io.github.medianik.starter.telegram.filter.CommandRequest
import io.github.medianik.starter.telegram.filter.CommandResponse
import io.github.medianik.starter.telegram.filter.FilterContext

/**
 * Handles BotExecutionException
 */
interface ExecutionExceptionHandler {
    /**
     * Handles BotExecutionException with context that was in filters chain
     *
     * @return true if exception was handled, false if exception should be handled by next handler
     */
    suspend fun handle(
        exception: BotExecutionException,
        context: FilterContext,
        request: CommandRequest,
        response: CommandResponse,
    ): Boolean
}