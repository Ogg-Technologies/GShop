package com.example.gshop.model.utilities

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
