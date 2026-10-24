package fi.kroon.vadret.weatherforecast

import fi.kroon.vadret.common.Failure
import io.github.sphrak.either.Either

internal interface GetLocationModeTask {

    /**
     *  True while the forecast follows the device location.
     */
    suspend operator fun invoke(): Either<Failure, Boolean>
}