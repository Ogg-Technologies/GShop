package com.example.gshop.model.utilities.monads

typealias ParseResult<T> = SuccessfulParseResult<T>?

data class SuccessfulParseResult<out T>(val value: T, val remaining: String)

typealias Parser<T> = (String) -> ParseResult<T>

private object ParserShortCircuit : Throwable()

fun <T> parser(block: ParserScope.() -> Parser<T>): Parser<T> = { input ->
    try {
        ParserScope(input).block().invoke(input)
    } catch (e: ParserShortCircuit) {
        null
    }
}

class ParserScope(var string: String) {
    fun <T> Parser<T>.bind(): T = invoke(string).let {
        if (it == null) throw ParserShortCircuit
        else {
            string = it.remaining
            it.value
        }
    }

    fun <T> pure(value: T): Parser<T> = { ParseResult(value, string) }
}

fun charSatisfying(predicate: (Char) -> Boolean): Parser<Char> = { input ->
    input.firstOrNull()?.let { if (predicate(it)) ParseResult(it, input.drop(1)) else null }
}

val anyChar: Parser<Char> = charSatisfying { true }

val digit: Parser<Char> = charSatisfying { it.isDigit() }

fun char(c: Char): Parser<Char> = charSatisfying { it == c }

infix fun <T> Parser<T>.or(other: Parser<T>): Parser<T> = { input -> invoke(input) ?: other(input) }

infix fun <A, B> Parser<A>.then(other: Parser<B>): Parser<Pair<A, B>> = { input ->
    invoke(input)?.let { first ->
        other(first.remaining)?.let { second ->
            ParseResult(first.value to second.value, second.remaining)
        }
    }
}

fun <T, R> Parser<T>.map(f: (T) -> R): Parser<R> = { input ->
    invoke(input)?.let { ParseResult(f(it.value), it.remaining) }
}

infix fun <T> Parser<T>.prependedTo(other: Parser<List<T>>): Parser<List<T>> =
    (this then other).map { (first, rest) -> listOf(first) + rest }

fun <T> Parser<T>.repeatedBetween(min: Int, max: Int?): Parser<List<T>> {
    require(min >= 0)
    if (max != null) require(max >= min)
    return { input ->
        val result = mutableListOf<T>()
        fun canConsumeMore() = max == null || result.size < max - 1
        fun consumedEnough() = result.size >= min
        var remaining = input
        var next = invoke(remaining)
        while (next != null && canConsumeMore()) {
            result += next.value
            remaining = next.remaining
            next = invoke(remaining)
        }
        if (consumedEnough()) ParseResult(result, remaining) else null
    }
}

fun <T> zeroOrMore(parser: Parser<T>): Parser<List<T>> = parser.repeatedBetween(0, null)

fun <T> oneOrMore(parser: Parser<T>): Parser<List<T>> = parser.repeatedBetween(1, null)

infix fun <A, B> Parser<A>.right(other: Parser<B>): Parser<B> = (this then other).map { it.second }

infix fun <A, B> Parser<A>.left(other: Parser<B>): Parser<A> = (this then other).map { it.first }

fun <E, S> chain(element: Parser<E>, separator: Parser<S>): Parser<List<E>> =
    (element prependedTo zeroOrMore(separator right element))

fun <L, T, R> Parser<T>.surroundedBy(before: Parser<L>, after: Parser<R>): Parser<T> =
    before right this left after

fun <L, T, R> Parser<T>.surroundedBy(around: Pair<Parser<L>, Parser<R>>): Parser<T> =
    around.first right this left around.second

infix fun <S, T> Parser<T>.surroundedBy(around: Parser<S>): Parser<T> = surroundedBy(around, around)

fun string(s: String): Parser<String> =
    { input -> if (input.startsWith(s)) ParseResult(s, input.drop(s.length)) else null }

/**
 * Parse with [this] ONLY if [other] fails.
 */
infix fun <T> Parser<T>.ifNot(other: Parser<Any>): Parser<T> =
    { input -> if (other(input) == null) invoke(input) else null }

/**
 * Parse with [this] com.example.gshop.model.utilities.monads.then parse with [other] without consuming any input.
 * If [other] succeeds, return the result from [this].
 */
infix fun <T> Parser<T>.lookAhead(other: Parser<Any>): Parser<T> =
    { input -> invoke(input)?.let { if (other(it.remaining) != null) it else null } }

fun <T> Parser<T>.repeatedUntil(end: Parser<Any>): Parser<List<T>> =
    zeroOrMore(this ifNot end) lookAhead end