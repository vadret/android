package fi.kroon.vadret.data.exception

import fi.kroon.vadret.data.failure.Failure

/**
 *  Translates [Throwable] to [Failure]. Usually
 *  for .onErrorReturn { } in RxJava.
 */
interface IExceptionHandler<F : Failure> {
    operator fun invoke(throwable: Throwable): F
}