package com.example.gshop.model.utilities.monads

import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract

/**
 * Generic result class which hold either a value of type [T] or an error of type [E].
 */
@OptIn(ExperimentalContracts::class)
sealed class GResult<out T, out E> {
    data class Ok<T>(val value: T) : GResult<T, Nothing>()
    data class Err<E>(val error: E) : GResult<Nothing, E>()

    fun isOk(): Boolean {
        contract {
            returns(true) implies (this@GResult is Ok<T>)
            returns(false) implies (this@GResult is Err<E>)
        }
        return this is Ok<T>
    }

    fun isErr(): Boolean {
        contract {
            returns(false) implies (this@GResult is Ok<T>)
            returns(true) implies (this@GResult is Err<E>)
        }
        return this is Err<E>
    }

    fun unwrap(): T = when (this) {
        is Ok -> value
        is Err -> throw IllegalStateException(error.toString())
    }
}

inline fun <O, T, E> GResult<T, E>.flatMap(f: (T) -> GResult<O, E>) = when (this) {
    is GResult.Ok -> f(value)
    is GResult.Err -> this
}

inline fun <T, E> GResult<T, E>.map(f: (T) -> T): GResult<T, E> = flatMap { GResult.Ok(f(it)) }

class GResultScope {
    fun <T, E> GResult<T, E>.bind(): T = when (this) {
        is GResult.Ok -> value
        is GResult.Err -> throw GResultShortCircuit(error)
    }
}

private class GResultShortCircuit(val error: Any?) : Throwable()

/**
 * Creates a do-notation scope for a [GResult]. This allows you to call bind() on a [GResult]
 * to short circuit the execution if the result is an error.
 */
fun <T, E> gresult(block: GResultScope.() -> GResult<T, E>): GResult<T, E> = try {
    GResultScope().block()
} catch (e: GResultShortCircuit) {
    // e.error has to be Any because kotlin does not allow generics in Throwable
    @Suppress("UNCHECKED_CAST") GResult.Err(e.error as E)
}

/**
 * Creates a suspended do-notation scope for a [GResult]. It works like [gresult] but it allows
 * [block] to be a suspended function.
 */
suspend fun <T, E> suspendedGResult(block: suspend GResultScope.() -> GResult<T, E>): GResult<T, E> =
    try {
        GResultScope().block()
    } catch (e: GResultShortCircuit) {
        // e.error has to be Any because kotlin does not allow generics in Throwable
        @Suppress("UNCHECKED_CAST") GResult.Err(e.error as E)
    }
