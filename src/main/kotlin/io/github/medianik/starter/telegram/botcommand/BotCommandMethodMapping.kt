package io.github.medianik.starter.telegram.botcommand

import dev.inmo.tgbotapi.types.chat.Chat
import dev.inmo.tgbotapi.types.chat.User
import io.github.medianik.starter.telegram.annotation.BotCommand
import io.github.medianik.starter.telegram.annotation.BotHandler
import io.github.medianik.starter.telegram.annotation.RequiredState
import io.github.medianik.starter.telegram.botcommand.statemachine.StateMachineHolder
import org.springframework.aop.support.AopUtils
import org.springframework.beans.factory.InitializingBean
import org.springframework.context.ApplicationContext
import org.springframework.context.EmbeddedValueResolverAware
import org.springframework.context.support.ApplicationObjectSupport
import org.springframework.core.MethodIntrospector
import org.springframework.core.Ordered
import org.springframework.core.annotation.AnnotatedElementUtils
import org.springframework.util.ClassUtils
import org.springframework.util.LinkedMultiValueMap
import org.springframework.util.StringValueResolver
import java.lang.reflect.Method
import kotlin.reflect.KClass
import kotlin.reflect.KFunction
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.jvm.kotlinFunction

/**
 * Bean name prefix for target beans behind scoped proxies. Used to exclude those
 * targets from handler method detection, in favor of the corresponding proxies.
 *
 * We're not checking the autowire-candidate status here, which is how the
 * proxy target filtering problem is being handled at the autowiring level,
 * since autowire-candidate may have been turned to `false` for other
 * reasons, while still expecting the bean to be eligible for handler methods.
 *
 * Originally defined in [org.springframework.aop.scope.ScopedProxyUtils]
 * but duplicated here to avoid a hard dependency on the spring-aop module.
 */
private const val SCOPED_TARGET_NAME_PREFIX = "scopedTarget."

@Suppress("MemberVisibilityCanBePrivate")
open class BotCommandMethodMapping : ApplicationObjectSupport(), Ordered, EmbeddedValueResolverAware, InitializingBean {

    override fun afterPropertiesSet() {
        initHandlerMethods()
    }

    private fun initHandlerMethods() {
        for (beanName: String in getCandidateBeanNames()) {
            if (shouldNotHandle(beanName)) {
                continue
            }
            processCandidateBean(beanName)
        }
    }

    private fun getCandidateBeanNames(): Array<String> {
        return obtainApplicationContext().getBeanNamesForType(Any::class.java)
    }

    protected fun shouldNotHandle(beanName: String): Boolean {
        return !beanName.startsWith(SCOPED_TARGET_NAME_PREFIX)
    }

    private fun processCandidateBean(beanName: String) {
        try {
            val beanType: KClass<*>? = obtainApplicationContext().getType(beanName)?.kotlin
            if (beanType != null && isHandler(beanType)) {
                detectHandlerMethods(beanType, beanName)
            }
        } catch (ex: Throwable) {
            // An unresolvable bean type, probably from a lazy bean - let's ignore it.
            logger.trace("Could not resolve type for bean with name '$beanName'", ex)
        }
    }

    protected fun isHandler(beanType: KClass<*>): Boolean {
        return AnnotatedElementUtils.hasAnnotation(beanType::class.java, BotHandler::class.java)
    }

    private fun detectHandlerMethods(beanType: KClass<*>, beanName: String) {
        val userType = ClassUtils.getUserClass(beanType).kotlin
        val methods: Map<Method, BotCommandMethodInfo> =
            MethodIntrospector.selectMethods<BotCommandMethodInfo>(userType.java) { method: Method ->
                try {
                    return@selectMethods getMappingForMethod(
                        method.kotlinFunction
                            ?: throw IllegalStateException("Function cannot be represented as KFunction: $method"),
                        userType
                    )
                } catch (ex: Throwable) {
                    throw IllegalStateException(
                        "Invalid mapping on handler class [${userType.qualifiedName}]: $method",
                        ex
                    )
                }
            }
        logger.trace("Found methods: ${methods.map { it.key.toString() to it.value }.joinToString("\n")}")
        for ((method, info) in methods) {
            val invocableMethod = AopUtils.selectInvocableMethod(method, userType.java)
            registerHandlerMethod(
                beanName,
                beanType,
                invocableMethod.kotlinFunction
                    ?: throw IllegalStateException("Function cannot be represented as KFunction: $invocableMethod"),
                info
            )
        }
    }

    private fun registerHandlerMethod(beanName: String, beanType: KClass<*>, kFunction: KFunction<*>, info: BotCommandMethodInfo) {
        val handlerMethod = HandlerMethod.of(beanName, obtainApplicationContext(), beanType, kFunction)


    }

    private fun getA(): StateMachineHolder {
        TODO("Not yet implemented")
    }

    private fun getMappingForMethod(
        method: KFunction<*>,
        userType: KClass<*>,
    ): BotCommandMethodInfo {
        val requiredState = method.findAnnotation<RequiredState>()?.key
        val stateMachineHolder: StateMachineHolder = getA()
        if(requiredState != null) {
            val userMatcher: UserMatcher = { user, chat ->

            }
        }
        return botCommandMethodInfoBuilder(
            name = method.name,
            command = method.findAnnotation<BotCommand>()?.command,
            stateMachineKey = requiredState,
            argumentsMatcher = getArgumentsMatcher(method),
        )
    }

    private fun getArgumentsMatcher(method: KFunction<*>): (List<String>) -> Boolean {
        obtainApplicationContext().getBean(ArgumentsMatcher)
    }

    override fun getOrder(): Int {
        return 0
    }

    override fun setEmbeddedValueResolver(resolver: StringValueResolver) {
        TODO("Not yet implemented")
    }
}

class CommandRegistry internal constructor() {
    private val map = mutableMapOf<BotCommandMethodInfo, CommandMapping>()
    private val command = LinkedMultiValueMap<String, CommandMapping>()
    private val stateMachines = LinkedMultiValueMap<String, CommandMapping>()
    fun register(commandMapping: CommandMapping){
        map[commandMapping.info] = commandMapping
        if(commandMapping.info.command != null){
            command.add(commandMapping.info.command, commandMapping)
        }
        if(commandMapping.info.stateMachineKey != null){
            stateMachines.add(commandMapping.info.stateMachineKey, commandMapping)
        }
    }
}

class CommandMapping(
    val info: BotCommandMethodInfo,
    val handlerMethod: HandlerMethod,
)

class HandlerMethod private constructor(
    private val beanName: String,
    private val applicationContext: ApplicationContext,
    private val beanType: KClass<*>,
    private val method: KFunction<*>,
) {
    companion object {
        fun of(beanName: String, applicationContext: ApplicationContext, beanType: KClass<*>, kFunction: KFunction<*>): HandlerMethod {
            return HandlerMethod(beanName, applicationContext, beanType, kFunction)
        }
    }
}


internal fun botCommandMethodInfoBuilder(
    name: String,
    command: String? = null,
    stateMachineKey: String? = null,
    userMatcher: UserMatcher = { _, _ -> true},
    argumentsMatcher: ArgumentsMatcher =  { _ -> true},
): BotCommandMethodInfo {
    return BotCommandMethodInfo(
        name = name,
        command = command,
        stateMachineKey = stateMachineKey,
        userMatcher = userMatcher,
        argumentsMatcher = argumentsMatcher,
    )
}
data class BotCommandMethodInfo(
    val name: String,
    val command: String?,
    val stateMachineKey: String?,
    val userMatcher: UserMatcher,
    val argumentsMatcher: ArgumentsMatcher,
)

typealias UserMatcher = (User, Chat) -> Boolean
typealias ArgumentsMatcher = (List<String>) -> Boolean

