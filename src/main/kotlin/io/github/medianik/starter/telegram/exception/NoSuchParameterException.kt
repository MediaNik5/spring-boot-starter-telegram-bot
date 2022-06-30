package io.github.medianik.starter.telegram.exception

class NoSuchParameterException(
    parameterName: String,
    parameterIndex: Int,
) : BotExecutionException(){
    override val message = "No such parameter: $parameterName at index $parameterIndex"
}