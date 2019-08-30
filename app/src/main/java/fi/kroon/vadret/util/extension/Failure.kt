package fi.kroon.vadret.util.extension

import fi.kroon.vadret.data.exception.Failure
import io.github.sphrak.either.Either

fun Failure.asLeft(): Either.Left<Failure> = Either.Left(this)
fun <T> T.asRight(): Either.Right<T> = Either.Right(this)