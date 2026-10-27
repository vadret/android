package fi.kroon.vadret.weatherforecast

import fi.kroon.vadret.common.Failure
import io.github.sphrak.either.Either

internal interface GetLocationModeTask {

    suspend operator fun invoke(): Either<Failure, Boolean>
}