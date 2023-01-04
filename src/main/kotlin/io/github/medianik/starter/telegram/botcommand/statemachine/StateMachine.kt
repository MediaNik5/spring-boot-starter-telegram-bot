package io.github.medianik.starter.telegram.botcommand.statemachine

import dev.inmo.tgbotapi.types.Identifier
import java.util.concurrent.ConcurrentHashMap


class StateMachineHolder {
    private val currentStates = ConcurrentHashMap<StateContext, StateMachine<*>>()
    fun <T> getState(stateContext: StateContext, key: String): StateMachine<T>? {
        val state = currentStates[stateContext] ?: return null
        if (state.key != key) return null

        @Suppress("UNCHECKED_CAST")
        return state as StateMachine<T>
    }
    fun <T> nextState(stateContext: StateContext, nextKey: String, nextValue: T) {
        currentStates[stateContext] = StateMachine(nextKey, nextValue)
    }
}

data class StateMachine<V> (val key: String, val value: V)

class StateContext(val userId: Identifier, val chatId: Identifier){
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is StateContext) return false

        if (userId != other.userId) return false
        if (chatId != other.chatId) return false

        return true
    }

    override fun hashCode(): Int {
        var result = userId.hashCode()
        result = 31 * result + chatId.hashCode()
        return result
    }
}