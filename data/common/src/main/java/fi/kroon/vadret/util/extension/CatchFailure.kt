package fi.kroon.vadret.util.extension

import fi.kroon.vadret.data.exception.IExceptionHandler
import fi.kroon.vadret.data.failure.Failure
import io.github.sphrak.either.Either
import kotlin.coroutines.cancellation.CancellationException

/**
 *  Runs [block] and translates any exception it throws
 *  into a [Failure]. Cancellation is rethrown so that
 *  structured concurrency keeps working.
 */
inline fun <T> IExceptionHandler<Failure>.catchFailure(block: () -> Either<Failure, T>): Either<Failure, T> =
    try {
        block()
    } catch (exception: CancellationException) {
        throw exception
    } catch (exception: Exception) {
        invoke(exception).asLeft()
    }