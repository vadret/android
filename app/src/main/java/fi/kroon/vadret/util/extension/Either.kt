package fi.kroon.vadret.util.extension

import io.github.sphrak.either.Either
import io.reactivex.Single

inline fun <T, L, R> Either<L, R>.flatMapSingle(fn: (R) -> Single<Either<L, T>>): Single<Either<L, T>> =
    when (this) {
        is Either.Left -> Single.just(Either.Left(a))
        is Either.Right -> fn(b)
    }