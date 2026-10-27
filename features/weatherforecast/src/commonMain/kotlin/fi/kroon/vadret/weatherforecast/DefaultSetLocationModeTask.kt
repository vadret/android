package fi.kroon.vadret.weatherforecast

import fi.kroon.vadret.common.Failure
import fi.kroon.vadret.location.LocationRepository
import io.github.sphrak.either.Either

internal class DefaultSetLocationModeTask(
    private val locationRepository: LocationRepository
) : SetLocationModeTask {

    override suspend fun invoke(isAutomatic: Boolean): Either<Failure, Unit> = locationRepository.setAutomatic(isAutomatic = isAutomatic)
}