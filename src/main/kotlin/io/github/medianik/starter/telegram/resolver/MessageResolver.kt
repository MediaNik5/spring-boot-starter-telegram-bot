package io.github.medianik.starter.telegram.resolver

import dev.inmo.tgbotapi.extensions.behaviour_builder.BehaviourContext
import dev.inmo.tgbotapi.types.message.abstracts.CommonMessage
import dev.inmo.tgbotapi.types.message.content.MessageContent
import io.github.medianik.starter.telegram.annotation.param.MessageValue
import io.github.medianik.starter.telegram.exception.InvalidTypeException
import io.github.medianik.starter.telegram.filter.CommandRequest
import io.github.medianik.starter.telegram.filter.CommandResponse
import io.github.medianik.starter.telegram.filter.FilterContext
import io.github.medianik.starter.telegram.filter.filters.CommandParameterFilter
import io.github.medianik.starter.telegram.util.clazz
import io.github.medianik.starter.telegram.util.hasAnnotationInherited
import io.github.medianik.starter.telegram.util.throwExceptionIfNotIgnored
import org.springframework.stereotype.Component
import kotlin.reflect.KClass
import kotlin.reflect.KFunction
import kotlin.reflect.KParameter
import kotlin.reflect.full.isSubclassOf

private typealias MessageClass = dev.inmo.tgbotapi.types.message.abstracts.Message

@Component
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
        context: FilterContext,
        request: CommandRequest,
        response: CommandResponse,
        parameter: KParameter,
    ): Any {
        val clazz = parameter.clazz
        if(clazz == Long::class){
            return request.incomingMessage.messageId
        }
        if(clazz.isSubclassOf(MessageClass::class)){
            return request.incomingMessage
        }

        throw IllegalStateException("Cannot happen")
    }
}

private fun oneOfMessageTypes(clazz: KClass<*>): Boolean{
    return clazz.isSubclassOf(MessageClass::class) || clazz == Long::class
}