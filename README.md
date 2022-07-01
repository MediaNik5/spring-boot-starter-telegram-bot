# Spring boot starter for telegram bot

This project is intended to improve your development experience 
of telegram bots with power of Spring Boot.

This starter lets you write code in controller-like style for handling 
telegram bot commands.

##### Table of contents
- [How to use](#how-to-use)
- [Principles](#principles)
  * [Controller-likeness](#controller-likeness)
  * [Return value](#return-value)
- [Annotations](#annotations)
  * [Parameters](#parameters)
    + [@Param](#param)
    + [@BotValue](#botvalue)
    + [@ChatValue](#chatvalue)
    + [@MessageValue](#messagevalue)
    + [@SendDateValue](#senddatevalue)
    + [@UserValue](#uservalue)
    + [@RemainingParams](#remainingparams)
    + [@WholeTextValue](#wholetextvalue)
  * [Other annotations](#other-annotations)
    + [@IgnoreException](#ignoreexception)
    + [@NoReply](#noreply)
- [Default values](#default-values)
- [Examples](#examples)
----

# How to use

You can initialize project with [Spring Initialzr](https://start.spring.io/) with 
kotlin as language and gradle project

Add this to your dependencies in `build.gradle.kts`:
```gradle
dependencies {
    val tgbotapi_version = "2.1.0"
    implementation("dev.inmo:tgbotapi:$tgbotapi_version")
    implementation("dev.inmo:tgbotapi.core:$tgbotapi_version")
    implementation("dev.inmo:tgbotapi.api:$tgbotapi_version")
    implementation("dev.inmo:tgbotapi.utils:$tgbotapi_version")
    implementation("org.springframework.boot:spring-boot-starter")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("io.github.medianik5:spring-boot-starter-telegram-bot:0.1")
}
```

And apply few plugins:
```gradle
plugins {
    id("org.springframework.boot") version "2.7.1"
    id("io.spring.dependency-management") version "1.0.11.RELEASE"
    kotlin("jvm") version "1.6.21"
    kotlin("plugin.spring") version "1.6.21"
}
```

Or you could use `build.gradle`:
```gradle
def tgbotapi_version = "2.1.0"
dependencies {
    implementation "dev.inmo:tgbotapi:${tgbotapi_version}"
    implementation "dev.inmo:tgbotapi.core:${tgbotapi_version}"
    implementation "dev.inmo:tgbotapi.api:${tgbotapi_version}"
    implementation "dev.inmo:tgbotapi.utils:${tgbotapi_version}"
    implementation "org.springframework.boot:spring-boot-starter"
    implementation "org.springframework.boot:spring-boot-starter-validation"
    implementation "io.github.medianik5:spring-boot-starter-telegram-bot:0.1"
}
```

And apply few plugins:
```gradle
plugins {
    id "org.springframework.boot"  version "2.7.1"
    id "io.spring.dependency-management"  version "1.0.11.RELEASE"
    kotlin "jvm"  version "1.6.21"
    kotlin "plugin.spring"  version "1.6.21"
}
```

Create application.properties in resources folder and fill bot token and bot username,
get them from [BotFather](https://t.me/BotFather):
```properties
io.github.medianik.telegram.bot-token=
io.github.medianik.telegram.bot-username=
```


Create main class in non-default package:
```kotlin
package com.example.bot

@SpringBootApplication
class DemoApplication

fun main(args: Array<String>){
  runApplication<DemoApplication>(*args)
}
```

# Principles

## Controller-likeness

Every controller can be bot command handler.
For this purpose, you need to mark your `@Controller` class with
`@BotHandler`:

```kotlin
import io.github.medianik.starter.telegram.annotation.BotHandler
import org.springframework.stereotype.Controller

@Controller
@BotHandler
class SimpleBotController {
    // ...
}
```

Every command should be annotated with `@BotCommand`</br>
It must have at least one property command for command value. 
Command might consist only of english letters and underscores.
**Notice**: You cannot have two handlers handling same command.

```kotlin
...
import io.github.medianik.starter.telegram.annotation.BotCommand

@Controller
@BotHandler
class SimpleBotController {
    @BotCommand("/command")
    fun handleCommand(){
        // when somebody fires "/command" to your bot(or chat with your bot)
        // this function will be called
    }
}

```

## Return value

By default if you have String return value in bot handler, it
will be automatically be converted to reply message to user
who's send the message to bot.
```kotlin
@BotCommand("/command")
fun handleCommand(): String {
    // ...
    return "Hello, world!" // bot would reply "Hello, world!" to user
}
```
# Annotations

## Parameters

### Param

If you want to get some *params* that user has sent to bot 
alongside with command, you can use `@Param` annotation:

```kotlin
import io.github.medianik.starter.telegram.annotation.param.Param
...
@BotCommand("/command")
fun handleCommand(@Param name: String){
    // user sent message "/command name"
    // and now you have "name" in this scope
}
```

You can have multiple `@Param` annotations for same command.</br>
They will go in sequential order, meaning that first `@Param` will be
resolved with first word after command, second `@Param` will be resolved
with second word after command, and so on.

You may have any other input arguments inbetween ones with
`@Param` annotation, then the order for `@Param`s will be the same as
if there were no other arguments.

```kotlin
@BotCommand("/command")
fun handleCommand(@Param name: String, somethingVeryUseful: SomeType, @Param lastName: String){
    // for "/command Joe Baiden"
    // name would be "Joe", lastName would be "Baiden"
}
```

### BotValue

If you want to get instance of bot to do some difficult logic, 
you can use @BotValue on parameter to get it.</br>
If you specify type for parameter as TelegramBot, it will
be resolved as TelegramBot instance automatically.


```kotlin
import dev.inmo.tgbotapi.bot.TelegramBot
...
@BotCommand("/command")
fun handleCommand(bot: TelegramBot){
    // you can use `bot` here...
}
```

### ChatValue

You can get chat instance or chat id for message that was sent to bot.</br>
If you use Chat type, you might not specify the type. If you
want to get chat id, you have to use type Long and annotation `@ChatValue`.

```kotlin
import io.github.medianik.starter.telegram.annotation.param.ChatValue
import dev.inmo.tgbotapi.types.chat.Chat
...
@BotCommand("/command")
fun handleCommand(chat: Chat){
    // you can use `chat` here...
}
@BotCommand("/command1")
fun handleCommand1(@ChatValue chatId: Long){
    // you can use `chatId` here...
}
```

### MessageValue

If you want to get message instance, you can specify type Message for param
(`@MessageValue` is optional).</br>
If you want to get message id, you have to use type Long and annotation `@MessageValue`.

```kotlin
import io.github.medianik.starter.telegram.annotation.param.MessageValue
import dev.inmo.tgbotapi.types.message.abstracts.CommonMessage
import dev.inmo.tgbotapi.types.message.content.MessageContent

@BotCommand("/command")
fun handleCommand(message: CommonMessage<out MessageContent>){
    // you can use `message` here...
}

@BotCommand("/command1")
fun handleCommand1(@MessageValue messageId: Long){
    // you can use `messageId` here...
}
```

### SendDateValue

If you want to get DateTime when message was sent, use @SendDateValue annotation.
Supported types are: java's `Instant`, `LocalDate`, `LocalTime`, `LocalDateTime`,
and third party's `com.soywiz.klock.DateTime`

```kotlin
import io.github.medianik.starter.telegram.annotation.param.SendDateValue
import com.soywiz.klock.DateTime
...
@BotCommand("/command")
fun handleCommand(@SendDateValue date: DateTime){
    // you can use `date` here...
}
```

### UserValue

If you want to get User instance, you can specify type User for param
(`@UserValue` is optional).</br>
If you want to get User id, you have to use type Long and annotation `@UserValue`.

```kotlin
import io.github.medianik.starter.telegram.annotation.param.UserValue
import dev.inmo.tgbotapi.types.chat.User
...
@BotCommand("/command")
fun handleCommand(user: User){
    // you can use `user` here...
}

@BotCommand("/command1")
fun handleCommand1(@UserValue userId: Long){
    // you can use `userId` here...
}
```

### RemainingParams

If you want to get remaining params after those that you have specified before,
you can use `@RemainingParams` annotation.

For example, for input `/command param0 param1 param2 param3`
and function 
```kotlin
@BotCommand("/command")
fun handleCommand(
    @Param param: String, 
    @RemainingParams params: String, 
    @Param param1
){
    // param will be "param0", params will be "param1 param2 param3"
    // and param1 will be "param1", so you can combine them
}
```

### WholeTextValue

Gets whole text that was sent to bot.

For example for input `/command param0 param1 param2 param3`
```kotlin
@BotCommand("/command")
fun handleCommand(@WholeTextValue text: String){
    // text will be `/command param0 param1 param2 param3`
    // if you want to get only params, use @RemainingParams as first param
}
```

## Other annotations

### IgnoreException

Ignore exception if it happens.

For example ignore that there are two handlers for same command.</br>
**Notice**: in this case any of the handler might be called. The order is not guaranteed. 

```kotlin
import io.github.medianik.starter.telegram.annotation.IgnoreException
import io.github.medianik.starter.telegram.exception.DuplicateBotCommandException

@IgnoreException(DuplicateBotCommandException::class)
@BotCommand("/command")
fun handleCommand(){
    // ...
}

@IgnoreException(DuplicateBotCommandException::class)
@BotCommand("/command")
fun handleCommand1(){
    // ...
}
```

### NoReply

If you want to have return type of function to be `String`, but
dont want to reply to user, you can use `@NoReply` annotation.

```kotlin
import io.github.medianik.starter.telegram.annotation.NoReply
...
@NoReply
@BotCommand("/command")
fun handleCommand(): String{
    return "Ignored String"
}
```


# Default values

You can use power of kotlin and use default values for any argument
in your command handler, then if there was not found any appropriate
value for that argument, the default value will be used.

```kotlin
@BotCommand("/command")
fun handleCommand(@Param name: String = "Michael", @Param lastName: String = "Obama"){
    // for "/command Joe"
    // name would be "Joe", lastName would be "Obama"
    
    // for "/command"
    // both parameters would be defaulted to "Michael" and "Obama"
}
```



-----

# Examples

Simple command handler
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

More complex command handler with remembering the users
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