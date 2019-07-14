package fi.kroon.vadret.data.exception

import fi.kroon.vadret.data.functional.Either
import fi.kroon.vadret.di.scope.CoreApplicationScope
import fi.kroon.vadret.util.extension.asLeft
import fi.kroon.vadret.util.extension.asSingle
import io.reactivex.Single

@CoreApplicationScope
class ErrorHandler : IErrorHandler {

    override fun <T> getNetworkError(throwable: Throwable): Single<Either<Failure, T>> =
        Failure
            .NetworkError("error: network request failed: ${throwable.message}, caused by: ${throwable.cause}")
            .asLeft()
            .asSingle()

    override fun <T> getNetworkOfflineError(): Single<Either<Failure, T>> =
        Failure
            .NetworkOfflineError("error: network offline or not available")
            .asLeft()
            .asSingle()

    override fun <T, V> getLocalKeyValueWriteError(key: String, value: V): Single<Either<Failure, T>> =
        Failure
            .LocalKeyValueWriteError("error: failed writing \'$value\'. key was not recognized: \'$key\'")
            .asLeft()
            .asSingle()

    override fun <T> getLocalKeyValueReadError(key: String): Single<Either<Failure, T>> =
        Failure
            .LocalKeyValueReadError("error: failed reading property for key \'$key\'. It was not recognized.")
            .asLeft()
            .asSingle()

    override fun <T> getCacheReadError(): Single<Either<Failure, T>> =
        Failure
            .CacheReadError("error: failed reading from cache")
            .asLeft()
            .asSingle()

    override fun <T> getCacheWriteError(): Single<Either<Failure, T>> =
        Failure
            .CacheWriteError("error: failed writing to cache")
            .asLeft()
            .asSingle()
}