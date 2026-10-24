package fi.kroon.vadret.weatherforecast

import fi.kroon.vadret.common.Failure
import fi.kroon.vadret.location.LocationRepository
import io.github.sphrak.either.Either
import javax.inject.Inject

internal class DefaultGetLocationModeTask @Inject constructor(
    private val locationRepository: LocationRepository
) : GetLocationModeTask {

    override suspend fun invoke(): Either<Failure, Boolean> =
        locationRepository.isAutomatic()
}