package io.github.medianik.starter.telegram.exception


class DuplicateBotCommandException(command: String) : BotCommandException() {
    override val message: String = "Command $command is defined at least in two places"
}