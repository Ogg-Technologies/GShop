package com.example.gshop.model

import com.example.gshop.redux.*
import kotlinx.coroutines.delay
import kotlinx.serialization.Serializable

val appStore = Store(
    initialState = State(),
    rootReducer = ::rootReducer,
    middlewares = listOf(
        loggerMiddleware,
        thunkMiddleware,
    )
)

@Serializable
data class State(val number: Int = 0)

object Increment : Action

fun rootReducer(state: State, action: Action): State = when (action) {
    is Increment -> state.copy(number = state.number + 1)
    else -> state
}

fun doIncrementStream(amount: Int, timeDelay: Long = 100) = AsyncThunk { state, dispatch ->
    repeat(amount) {
        delay(timeDelay)
        dispatch(Increment)
    }
}