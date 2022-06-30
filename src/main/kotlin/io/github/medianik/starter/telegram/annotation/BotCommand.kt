package io.github.medianik.starter.telegram.annotation

/**
 * Marks the method as a command handler.
 *
 * The method is being called when the string specified in command field is received by bot.
 * For example, if command is "/start", the method is being called when any string starting with "/start" in any chat is received.
 *
 * This annotation has to be used inside Spring Boot bean that is annotated with @[BotHandler].
 *
 * @param command The command name, identifier to be used to determine the command which this method corresponds to. Might start with '/' or not.
 * @param description The command description.
 * @param aliases The command aliases.
 * @see [BotHandler]
 */
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
@MustBeDocumented
annotation class BotCommand(
    /**
     * The command name, identifier to be used to determine the command which this method corresponds to.
     * Might start with '/' or not.
     */
    val command: String,
    val description: String = "",
    val example: String = "",
)

operator fun BotCommand.component1(): String = command
operator fun BotCommand.component2(): String = description
operator fun BotCommand.component3(): String = example