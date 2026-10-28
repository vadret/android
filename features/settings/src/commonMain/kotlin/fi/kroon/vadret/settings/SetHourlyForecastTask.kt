package fi.kroon.vadret.settings

import fi.kroon.vadret.common.Failure
import io.github.sphrak.either.Either

internal interface SetHourlyForecastTask {
    suspend operator fun invoke(isHourlyForecastEnabled: Boolean): Either<Failure, Unit>
}