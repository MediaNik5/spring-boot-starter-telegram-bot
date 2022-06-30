package io.github.medianik.starter.telegram.filter

import dev.inmo.tgbotapi.extensions.behaviour_builder.BehaviourContext
import dev.inmo.tgbotapi.types.message.abstracts.CommonMessage
import dev.inmo.tgbotapi.types.message.content.MessageContent
import io.github.medianik.starter.telegram.annotation.FilterAfter
import kotlin.reflect.KFunction
import kotlin.reflect.KType

@FilterAfter(CommandLauncherFilter::class, 100u)
interface CommandReturnValueFilter : CommandFilter {
    override fun isApplicable(function: KFunction<*>): Boolean {
        return supportsReturnType(function.returnType, function)
    }

    fun supportsReturnType(returnType: KType, function: KFunction<*>): Boolean

    override suspend fun filter(context: FilterContext, request: CommandRequest, response: CommandResponse) {
        response.result =
            processReturnValue(context.bot, request.incomingMessage, context.command.function, response.result)
    }

    suspend fun processReturnValue(
        bot: BehaviourContext,
        incomingMessage: CommonMessage<out MessageContent>,
        function: KFunction<*>,
        returnValue: Any?,
    ): Any?
}
