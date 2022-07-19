package com.example.gshop.model.utilities

import kotlinx.coroutines.withTimeoutOrNull

suspend inline fun <T> withTimeoutOrDefault(
    timeMillis: Long?,
    default: T,
    crossinline block: suspend () -> T,
): T = if (timeMillis == null) block() else withTimeoutOrNull(timeMillis) { block() } ?: default

