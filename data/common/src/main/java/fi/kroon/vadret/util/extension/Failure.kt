package fi.kroon.vadret.util.extension

import fi.kroon.vadret.data.failure.Failure
import io.github.sphrak.either.Either

fun Failure.asLeft(): Either<Failure, Nothing> = Either.Left(this)
fun <T> T.asRight(): Either<Nothing, T> = Either.Right(this)