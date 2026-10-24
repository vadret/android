package fi.kroon.vadret.util.extension

import io.reactivex.Single
import se.fimbla.either.Either
import se.fimbla.either.left

inline fun <T, L, R> Either<L, R>.flatMapSingle(fn: (R) -> Single<Either<L, T>>): Single<Either<L, T>> =
    when (this) {
        is Either.Left -> Single.just(left(value))
        is Either.Right -> fn(value)
    }