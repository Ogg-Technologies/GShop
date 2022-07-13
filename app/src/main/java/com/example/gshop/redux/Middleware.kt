package com.example.gshop.redux

import com.example.gshop.model.Database
import com.example.gshop.model.store.State
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

val Action.name: String
    get() = try {
        @Suppress("RECEIVER_NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
        javaClass.canonicalName.removePrefix(javaClass.`package`.name).removePrefix(".")
    } catch (e: Exception) {
        "UnknownActionName"
    }

val loggerMiddleware: Middleware<State> = { store, next, action ->
    next(action)
    println("New action: ${action.name} -> ${Json.encodeToString(store.state)}")
}

val persistentStorageMiddleware: Middleware<State> = { store, next, action ->
    next(action)
    val jsonState = Json.encodeToString(store.state)
    Database.writeJsonState(jsonState)
}

/**
 * An action that executes a function which can conditionally dispatch zero
 * or more actions based on the current state.
 */
class Thunk(val execute: (state: State, dispatch: Dispatch) -> Unit) : Action

typealias AsyncDispatch = suspend (action: Action) -> Unit

/**
 * An action that works like [Thunk], but also dispatches actions asynchronously.
 * The dispatch function will always execute on the IO thread.
 */
class AsyncThunk(val execute: suspend (state: State, dispatch: AsyncDispatch) -> Unit) : Action

val thunkMiddleware: Middleware<State> = { store, next, action ->
    when (action) {
        is Thunk -> action.execute(store.state, store.dispatch)
        is AsyncThunk -> dispatchAsyncThunk(store, action)
        else -> next(action)
    }
}

private fun dispatchAsyncThunk(store: Store<State>, asyncThunk: AsyncThunk) {
    GlobalScope.launch(Dispatchers.Main) {
        asyncThunk.execute(
            store.state
        ) { a ->
            withContext(Dispatchers.IO) {
                store.dispatch(a)
            }
        }
    }
}