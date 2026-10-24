package fi.kroon.vadret.data.exception

import fi.kroon.vadret.data.failure.Failure
import io.github.sphrak.either.Either

interface IErrorHandler {

    fun <T, V> getLocalKeyValueWriteError(key: String, value: V): Either<Failure, T>
    fun <T> getLocalKeyValueReadError(key: String): Either<Failure, T>

    fun <T> getCacheWriteError(): Either<Failure, T>
    fun <T> getCacheReadError(): Either<Failure, T>

    fun <T> getNetworkError(throwable: Throwable): Either<Failure, T>
    fun <T> getNetworkOfflineError(): Either<Failure, T>
}