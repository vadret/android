package fi.kroon.vadret.data.exception

import fi.kroon.vadret.data.failure.Failure
import io.github.sphrak.either.Either
import io.reactivex.Single

interface IErrorHandler {

    fun <T, V> getLocalKeyValueWriteError(key: String, value: V): Single<Either<Failure, T>>
    fun <T> getLocalKeyValueReadError(key: String): Single<Either<Failure, T>>

    fun <T> getCacheWriteError(): Single<Either<Failure, T>>
    fun <T> getCacheReadError(): Single<Either<Failure, T>>

    fun <T> getNetworkError(throwable: Throwable): Single<Either<Failure, T>>
    fun <T> getNetworkOfflineError(): Single<Either<Failure, T>>
}