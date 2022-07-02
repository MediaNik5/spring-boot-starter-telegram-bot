package io.github.medianik.starter.telegram.resolver

import dev.inmo.tgbotapi.extensions.behaviour_builder.BehaviourContext
import dev.inmo.tgbotapi.extensions.utils.extensions.raw.from
import dev.inmo.tgbotapi.types.chat.User
import dev.inmo.tgbotapi.types.message.abstracts.CommonMessage
import dev.inmo.tgbotapi.types.message.content.MessageContent
import dev.inmo.tgbotapi.utils.RiskFeature
import io.github.medianik.starter.telegram.annotation.param.UserValue
import io.github.medianik.starter.telegram.exception.InvalidTypeException
import io.github.medianik.starter.telegram.exception.NoFromUserException
import io.github.medianik.starter.telegram.filter.filters.CommandParameterFilter
import io.github.medianik.starter.telegram.util.clazz
import io.github.medianik.starter.telegram.util.hasAnnotationInherited
import io.github.medianik.starter.telegram.util.throwExceptionIfNotIgnored
import org.springframework.stereotype.Component
import kotlin.reflect.KClass
import kotlin.reflect.KFunction
import kotlin.reflect.KParameter
import kotlin.reflect.full.isSubclassOf

@Component
class FromUserResolver : CommandParameterFilter {
    companion object{
        private val expectedTypes = listOf(User::class, Long::class)
    }
    override fun supportsParameter(parameter: KParameter, function: KFunction<*>): Boolean {
        val clazz = parameter.clazz
        if(hasAnnotationInherited(parameter, UserValue::class)) {
            if(oneOfUserTypes(clazz)){
                return true
            }
            throwExceptionIfNotIgnored(function){
                InvalidTypeException(function, parameter, expectedTypes)
            }
            return false
        }
        return clazz.isSubclassOf(User::class)
    }

    @OptIn(RiskFeature::class)
    override suspend fun resolveParameter(
        bot: BehaviourContext,
        incomingMessage: CommonMessage<out MessageContent>,
        function: KFunction<*>,
        parameter: KParameter,
    ): Any? {
        val clazz = parameter.clazz
        val from = incomingMessage.from

        if(clazz.isSubclassOf(User::class)){
            if(from != null){
                return from
            }
        }
        if(clazz == Long::class){
            val fromId = from?.id?.chatId
            if(fromId != null){
                return fromId
            }
        }
        if(!parameter.isOptional){
            throwExceptionIfNotIgnored(function){
                NoFromUserException(incomingMessage)
            }
        }
        return null
    }
}
private fun oneOfUserTypes(clazz: KClass<*>): Boolean{
    return clazz.isSubclassOf(User::class) || clazz == Long::class
}