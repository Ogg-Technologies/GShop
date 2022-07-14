package com.example.gshop.model.store

import com.example.gshop.redux.Action
import com.example.gshop.redux.AsyncThunk
import kotlinx.coroutines.delay

fun doDelayedDispatch(
    action: Action,
    delay: Long = 1000,
) = AsyncThunk { _, dispatch ->
    delay(delay)
    dispatch(action)
}

fun <T> List<T>.remove(index: Int): List<T> = filterIndexed { i, _ -> i != index }

fun <T> List<T>.set(index: Int, newValue: T): List<T> =
    mapIndexed { i, value -> if (i == index) newValue else value }

fun <T> List<T>.edit(index: Int, transform: (T) -> T): List<T> =
    mapIndexed { i, value -> if (i == index) transform(value) else value }
