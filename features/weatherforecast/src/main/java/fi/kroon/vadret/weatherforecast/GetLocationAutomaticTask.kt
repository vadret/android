package fi.kroon.vadret.weatherforecast

import fi.kroon.vadret.data.failure.Failure
import fi.kroon.vadret.data.location.LocationRepository
import fi.kroon.vadret.data.location.model.Location
import io.github.sphrak.either.Either
import javax.inject.Inject

class GetLocationAutomaticTask @Inject constructor(
    private val locationRepository: LocationRepository
) {
    suspend operator fun invoke(): Either<Failure, Location> =
        locationRepository()
}