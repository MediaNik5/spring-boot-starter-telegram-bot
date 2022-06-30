package io.github.medianik.starter.telegram.resolver

import dev.inmo.tgbotapi.extensions.behaviour_builder.BehaviourContext
import dev.inmo.tgbotapi.types.message.abstracts.CommonMessage
import dev.inmo.tgbotapi.types.message.content.MessageContent
import io.github.medianik.starter.telegram.annotation.param.MessageValue
import io.github.medianik.starter.telegram.exception.InvalidTypeException
import io.github.medianik.starter.telegram.filter.CommandParameterFilter
import io.github.medianik.starter.telegram.util.clazz
import io.github.medianik.starter.telegram.util.hasAnnotationInherited
import io.github.medianik.starter.telegram.util.throwExceptionIfNotIgnored
import kotlin.reflect.KClass
import kotlin.reflect.KFunction
import kotlin.reflect.KParameter
import kotlin.reflect.full.isSubclassOf

private typealias MessageClass = dev.inmo.tgbotapi.types.message.abstracts.Message

class MessageResolver : CommandParameterFilter {
    companion object{
        private val expectedTypes = listOf(MessageClass::class, Long::class)
    }
    override fun supportsParameter(parameter: KParameter, function: KFunction<*>): Boolean {
        val clazz = parameter.clazz
        if(hasAnnotationInherited(parameter, MessageValue::class)){
            if(oneOfMessageTypes(clazz)){
                return true
            }
            throwExceptionIfNotIgnored(function){
                InvalidTypeException(function, parameter, expectedTypes)
            }
            return false
        }
        return clazz.isSubclassOf(MessageClass::class)
    }

    override suspend fun resolveParameter(
        bot: BehaviourContext,
        incomingMessage: CommonMessage<out MessageContent>,
        function: KFunction<*>,
        parameter: KParameter,
    ): Any {
        val clazz = parameter.clazz
        if(clazz == Long::class){
            return incomingMessage.messageId
        }
        if(clazz == MessageClass::class){
            return incomingMessage
        }

        throw IllegalStateException("Cannot happen")
    }
}

private fun oneOfMessageTypes(clazz: KClass<*>): Boolean{
    return clazz.isSubclassOf(MessageClass::class) || clazz == Long::class
}