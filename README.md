# Spring boot starter for telegram bot

This project is intended to improve your development experience 
of telegram bots with power of Spring Boot.

This starter lets you write code in controller-like style for handling 
telegram bot commands.

Examples:

```kotlin

import io.github.medianik.starter.telegram.annotation.BotCommand
import io.github.medianik.starter.telegram.annotation.BotHandler
import io.github.medianik.starter.telegram.annotation.param.Param
import org.springframework.stereotype.Controller

@Controller
@BotHandler
class SimpleBotController {
    /**
     * Command with one optional parameter
     *
     * This method replies to command "/hello [name]" with message "Hello, [name]!"
     * or "Hello, world!" if [name] is not specified by user.
     */
    @BotCommand("/hello", description = "Hellos user or world", example = "/hello [yourName]")
    fun sayHello(@Param name: String = "World"): String{
        return "Hello, $name!"
    }
}
```

```kotlin
import io.github.medianik.starter.telegram.annotation.BotCommand
import io.github.medianik.starter.telegram.annotation.BotHandler
import io.github.medianik.starter.telegram.annotation.param.Param
import io.github.medianik.starter.telegram.annotation.param.UserValue
import dev.inmo.tgbotapi.types.chat.User
import org.springframework.stereotype.Controller
import java.util.concurrent.ConcurrentHashMap


@Controller
@BotHandler
class ALittleMoreComplexBotController{
    private val users = ConcurrentHashMap<Long, String>()

    @BotCommand("/start", description = "Adds user to the list of registered users")
    fun start(user: User): String{
        val userName = users.putIfAbsent(user.id.chatId, user.firstName)
        return if(userName == null){
            "Welcome, ${user.firstName}!"
        }else{
            "You are already registered!"
        }
    }

    @BotCommand("/list", description = "Lists all registered users")
    fun list(): String {
        return "Registered users: ${users.values.joinToString(", ")}"
    }

    @BotCommand("/stop", description = "Removes user to the list of registered users")
    fun stop(@UserValue userId: Long): String{
        val user = users.remove(userId)
        return if(user != null){
            "Bye, ${user}!"
        }else{
            "You are not registered!"
        }
    }
}
```