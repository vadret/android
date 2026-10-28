package fi.kroon.vadret.forecastwarning

import fi.kroon.vadret.common.Failure
import io.github.sphrak.either.Either

internal interface MarkWarningSeenTask {

    suspend operator fun invoke(warningIdentifier: String): Either<Failure, Unit>
}