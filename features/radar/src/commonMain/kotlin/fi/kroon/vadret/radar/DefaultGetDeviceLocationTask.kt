package fi.kroon.vadret.radar

import fi.kroon.vadret.common.Failure
import fi.kroon.vadret.location.LocationRepository
import fi.kroon.vadret.location.model.Location
import io.github.sphrak.either.Either

internal class DefaultGetDeviceLocationTask(
    private val locationRepository: LocationRepository
) : GetDeviceLocationTask {

    override suspend fun invoke(): Either<Failure, Location> = locationRepository.getDeviceLocation()
}