package com.example.gshop.redux

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

typealias Dispatch = (action: Action) -> Unit
typealias Middleware<T> = (store: Store<T>, next: Dispatch, action: Action) -> Unit

interface Action

class Store<S>(
    initialState: S,
    private val rootReducer: (S, Action) -> S,
    middlewares: List<Middleware<S>> = emptyList(),
) {
    private val reduceAndUpdateState: Dispatch = { action ->
        val state = rootReducer(mutableStateFlow.value, action)
        mutableStateFlow.value = state
    }

    val dispatch: Dispatch = middlewares.foldRight(reduceAndUpdateState) { middleware, next ->
        { action ->
            middleware(this, next, action)
        }
    }

    private val mutableStateFlow: MutableStateFlow<S> = MutableStateFlow(initialState)
    val stateFlow: StateFlow<S> get() = mutableStateFlow.asStateFlow()
    val state: S get() = mutableStateFlow.value
}


