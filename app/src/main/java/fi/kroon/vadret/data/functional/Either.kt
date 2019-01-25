package fi.kroon.vadret.data.functional

import io.reactivex.Single

sealed class Either<out L0, out R0> {

    data class Left<out L1>(val a: L1) : Either<L1, Nothing>()
    data class Right<out R1>(val b: R1) : Either<Nothing, R1>()

    val isRight get() = this is Right<R0>
    val isLeft get() = this is Left<L0>

    fun <L2> left(a: L2) = Left(a)
    fun <R2> right(b: R2) = Right(b)

    fun <T0> either(fnL: (L0) -> T0, fnR: (R0) -> T0): T0 =
        when (this) {
            is Left -> fnL(a)
            is Right -> fnR(b)
        }
}

fun <A, B, C> ((A) -> B).c(fn: (B) -> C): (A) -> C = {
    fn(this(it))
}

fun <T, L, R> Either<L, R>.flatMapSingle(fn: (R) -> Single<Either<L, T>>): Single<Either<L, T>> =
    when (this) {
        is Either.Left -> Single.just(Either.Left(a))
        is Either.Right -> fn(b)
    }

fun <T, L, R> Either<L, R>.flatMap(fn: (R) -> Either<L, T>): Either<L, T> =
    when (this) {
        is Either.Left -> Either.Left(a)
        is Either.Right -> fn(b)
    }

fun <T, L, R> Either<L, R>.map(fn: (R) -> (T)): Either<L, T> = this.flatMap(fn.c(::right))