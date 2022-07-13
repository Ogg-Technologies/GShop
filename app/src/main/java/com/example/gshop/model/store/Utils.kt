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

