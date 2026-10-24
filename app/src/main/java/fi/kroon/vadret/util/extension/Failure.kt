package fi.kroon.vadret.util.extension

import fi.kroon.vadret.data.failure.Failure
import se.fimbla.either.Either
import se.fimbla.either.left
import se.fimbla.either.right

fun Failure.asLeft(): Either<Failure, Nothing> = left(this)
fun <T> T.asRight(): Either<Nothing, T> = right(this)