package fi.kroon.vadret.weatherforecast

import fi.kroon.vadret.common.Failure
import fi.kroon.vadret.location.LocationRepository
import io.github.sphrak.either.Either

internal class DefaultGetLocationModeTask(
    private val locationRepository: LocationRepository
) : GetLocationModeTask {

    override suspend fun invoke(): Either<Failure, Boolean> = locationRepository.isAutomatic()
}