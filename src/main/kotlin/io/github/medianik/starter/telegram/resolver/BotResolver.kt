package io.github.medianik.starter.telegram.resolver

import dev.inmo.tgbotapi.bot.TelegramBot
import dev.inmo.tgbotapi.extensions.behaviour_builder.BehaviourContext
import dev.inmo.tgbotapi.types.message.abstracts.CommonMessage
import dev.inmo.tgbotapi.types.message.content.MessageContent
import io.github.medianik.starter.telegram.filter.CommandParameterFilter
import io.github.medianik.starter.telegram.util.clazz
import kotlin.reflect.KClass
import kotlin.reflect.KFunction
import kotlin.reflect.KParameter

class BotResolver : CommandParameterFilter {
    override fun supportsParameter(parameter: KParameter, function: KFunction<*>): Boolean {
        return oneOfBotTypes(parameter.clazz)
    }

    override suspend fun resolveParameter(
        bot: BehaviourContext,
        incomingMessage: CommonMessage<out MessageContent>,
        function: KFunction<*>,
        parameter: KParameter,
    ): BehaviourContext {
        return bot
    }
}

private fun oneOfBotTypes(clazz: KClass<*>): Boolean{
    return clazz == TelegramBot::class || clazz == BehaviourContext::class
}