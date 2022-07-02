package io.github.medianik.starter.telegram.resolver

import dev.inmo.tgbotapi.bot.TelegramBot
import dev.inmo.tgbotapi.extensions.behaviour_builder.BehaviourContext
import dev.inmo.tgbotapi.types.message.abstracts.CommonMessage
import dev.inmo.tgbotapi.types.message.content.MessageContent
import io.github.medianik.starter.telegram.filter.CommandRequest
import io.github.medianik.starter.telegram.filter.CommandResponse
import io.github.medianik.starter.telegram.filter.FilterContext
import io.github.medianik.starter.telegram.filter.filters.CommandParameterFilter
import io.github.medianik.starter.telegram.util.clazz
import org.springframework.stereotype.Component
import kotlin.reflect.KClass
import kotlin.reflect.KFunction
import kotlin.reflect.KParameter

@Component
class BotResolver : CommandParameterFilter {
    override fun supportsParameter(parameter: KParameter, function: KFunction<*>): Boolean {
        return oneOfBotTypes(parameter.clazz)
    }

    override suspend fun resolveParameter(
        context: FilterContext,
        request: CommandRequest,
        response: CommandResponse,
        parameter: KParameter,
    ): BehaviourContext {
        return context.bot
    }
}

private fun oneOfBotTypes(clazz: KClass<*>): Boolean{
    return clazz == TelegramBot::class || clazz == BehaviourContext::class
}