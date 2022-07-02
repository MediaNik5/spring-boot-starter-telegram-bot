package io.github.medianik.starter.telegram.resolver

import dev.inmo.tgbotapi.extensions.behaviour_builder.BehaviourContext
import dev.inmo.tgbotapi.types.chat.User
import dev.inmo.tgbotapi.types.message.abstracts.CommonMessage
import dev.inmo.tgbotapi.types.message.content.MessageContent
import io.github.medianik.starter.telegram.annotation.param.ChatValue
import io.github.medianik.starter.telegram.exception.InvalidTypeException
import io.github.medianik.starter.telegram.filter.filters.CommandParameterFilter
import io.github.medianik.starter.telegram.util.clazz
import io.github.medianik.starter.telegram.util.hasAnnotationInherited
import io.github.medianik.starter.telegram.util.throwExceptionIfNotIgnored
import org.springframework.stereotype.Component
import kotlin.reflect.KClass
import kotlin.reflect.KFunction
import kotlin.reflect.KParameter
import kotlin.reflect.full.isSubclassOf

private typealias ChatClass = dev.inmo.tgbotapi.types.chat.Chat

@Component
class ChatResolver : CommandParameterFilter {
    companion object{
        private val expectedTypes = listOf(ChatClass::class, Long::class)
    }

    override fun supportsParameter(parameter: KParameter, function: KFunction<*>): Boolean {
        val clazz = parameter.clazz
        if(hasAnnotationInherited(parameter, ChatValue::class)) {
            if(oneOfChatTypes(clazz)){
                return true
            }
            throwExceptionIfNotIgnored(function){
                InvalidTypeException(function, parameter, expectedTypes)
            }
            return false
        }
        // if there is no annotation, check if the type is not User class
        return clazz.run {
            isSubclassOf(ChatClass::class) && !isSubclassOf(User::class)
        }
    }

    override suspend fun resolveParameter(
        bot: BehaviourContext,
        incomingMessage: CommonMessage<out MessageContent>,
        function: KFunction<*>,
        parameter: KParameter,
    ): Any {
        val clazz = parameter.clazz
        if(clazz == Long::class)
            return incomingMessage.chat.id.chatId
        if(clazz.isSubclassOf(ChatClass::class))
            return incomingMessage.chat
        throw InvalidTypeException(function, parameter, expectedTypes)
    }
}

private fun oneOfChatTypes(clazz: KClass<*>): Boolean {
    return (clazz.isSubclassOf(ChatClass::class)) ||
            clazz == Long::class
}