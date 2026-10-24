package fi.kroon.vadret.weatherforecast

import fi.kroon.vadret.common.Failure
import io.github.sphrak.either.Either

internal interface SetLocationModeTask {
    suspend operator fun invoke(isAutomatic: Boolean): Either<Failure, Unit>
}