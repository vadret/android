package fi.kroon.vadret.data.exception

import fi.kroon.vadret.data.failure.Failure

/**
 *  Translates [Throwable] to [Failure], see
 *  [fi.kroon.vadret.util.extension.catchFailure].
 */
interface IExceptionHandler<F : Failure> {
    operator fun invoke(throwable: Throwable): F
}