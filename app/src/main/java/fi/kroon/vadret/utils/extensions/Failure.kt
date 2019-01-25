package fi.kroon.vadret.utils.extensions

import fi.kroon.vadret.data.functional.Either
import fi.kroon.vadret.data.exception.Failure

fun Failure.asLeft(): Either.Left<Failure> = Either.Left(this)
fun <T> T.asRight(): Either.Right<T> = Either.Right(this)