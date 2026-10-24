package fi.kroon.vadret.data.exception

import fi.kroon.vadret.data.failure.Failure
import fi.kroon.vadret.util.extension.asLeft
import io.github.sphrak.either.Either

class ErrorHandler : IErrorHandler {

    override fun <T> getNetworkError(throwable: Throwable): Either<Failure, T> =
        Failure
            .NetworkError("error: network request failed: ${throwable.message}, caused by: ${throwable.cause}")
            .asLeft()

    override fun <T> getNetworkOfflineError(): Either<Failure, T> =
        Failure
            .NetworkOfflineError("error: network offline or not available")
            .asLeft()

    override fun <T, V> getLocalKeyValueWriteError(key: String, value: V): Either<Failure, T> =
        Failure
            .LocalKeyValueWriteError("error: failed writing \'$value\'. key was not recognized: \'$key\'")
            .asLeft()

    override fun <T> getLocalKeyValueReadError(key: String): Either<Failure, T> =
        Failure
            .LocalKeyValueReadError("error: failed reading property for key \'$key\'. It was not recognized.")
            .asLeft()

    override fun <T> getCacheReadError(): Either<Failure, T> =
        Failure
            .CacheReadError("error: failed reading from cache")
            .asLeft()

    override fun <T> getCacheWriteError(): Either<Failure, T> =
        Failure
            .CacheWriteError("error: failed writing to cache")
            .asLeft()
}