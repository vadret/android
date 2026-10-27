package fi.kroon.vadret.weatherforecast

import fi.kroon.vadret.common.Failure
import fi.kroon.vadret.favorite.model.Favorite
import fi.kroon.vadret.location.LocationRepository
import fi.kroon.vadret.weatherforecast.extension.toStoredLocation
import io.github.sphrak.either.Either

internal class DefaultSelectFavoriteTask(
    private val locationRepository: LocationRepository
) : SelectFavoriteTask {

    override suspend fun invoke(favorite: Favorite): Either<Failure, Unit> = locationRepository.selectStoredLocation(storedLocation = favorite.toStoredLocation())
}