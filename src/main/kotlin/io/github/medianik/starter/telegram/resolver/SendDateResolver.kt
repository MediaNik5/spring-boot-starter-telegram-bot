package io.github.medianik.starter.telegram.resolver

import com.soywiz.klock.DateTime
import dev.inmo.tgbotapi.extensions.behaviour_builder.BehaviourContext
import dev.inmo.tgbotapi.types.message.abstracts.CommonMessage
import dev.inmo.tgbotapi.types.message.content.MessageContent
import io.github.medianik.starter.telegram.annotation.param.SendDateValue
import io.github.medianik.starter.telegram.filter.CommandParameterFilter
import io.github.medianik.starter.telegram.util.clazz
import io.github.medianik.starter.telegram.util.hasAnnotationInherited
import org.springframework.stereotype.Component
import java.time.*
import kotlin.reflect.KClass
import kotlin.reflect.KFunction
import kotlin.reflect.KParameter

@Component
class SendDateResolver : CommandParameterFilter {
    override fun supportsParameter(parameter: KParameter, function: KFunction<*>): Boolean {
        return oneOfDateTypes(parameter.clazz) && hasAnnotationInherited(parameter, SendDateValue::class)
    }

    override suspend fun resolveParameter(
        bot: BehaviourContext,
        incomingMessage: CommonMessage<out MessageContent>,
        function: KFunction<*>,
        parameter: KParameter,
    ): Any? {
        return when(parameter.clazz){
            Instant::class -> Instant.ofEpochMilli(incomingMessage.date.unixMillisLong)
            LocalDate::class -> LocalDateTime.ofEpochSecond(incomingMessage.date.unixMillisLong, 0, ZoneOffset.UTC).toLocalDate()
            LocalTime::class -> LocalDateTime.ofEpochSecond(incomingMessage.date.unixMillisLong, 0, ZoneOffset.UTC).toLocalTime()
            LocalDateTime::class -> LocalDateTime.ofEpochSecond(incomingMessage.date.unixMillisLong, 0, ZoneOffset.UTC)
            DateTime::class -> DateTime(incomingMessage.date.unixMillisLong)
            else -> throw IllegalStateException("Cannot happen. Unsupported type: ${parameter.clazz}")
        }
    }
}

private fun oneOfDateTypes(clazz: KClass<*>): Boolean {
    return clazz == Instant::class ||
            clazz == LocalDateTime::class ||
            clazz == LocalDate::class ||
            clazz == LocalTime::class ||
            clazz == DateTime::class
}