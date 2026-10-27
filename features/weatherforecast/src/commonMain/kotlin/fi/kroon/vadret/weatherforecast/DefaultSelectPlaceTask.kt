package fi.kroon.vadret.weatherforecast

import fi.kroon.vadret.autocomplete.model.AutoCompleteItem
import fi.kroon.vadret.common.Failure
import fi.kroon.vadret.favorite.FavoriteRepository
import fi.kroon.vadret.favorite.model.Favorite
import fi.kroon.vadret.location.LocationRepository
import fi.kroon.vadret.weatherforecast.extension.toStoredLocation
import io.github.sphrak.either.Either
import io.github.sphrak.either.flatMap

internal class DefaultSelectPlaceTask(
    private val favoriteRepository: FavoriteRepository,
    private val locationRepository: LocationRepository
) : SelectPlaceTask {

    override suspend fun invoke(autoCompleteItem: AutoCompleteItem): Either<Failure, Unit> =
        favoriteRepository
            .addFavorite(
                locality = autoCompleteItem.locality,
                municipality = autoCompleteItem.municipality,
                county = autoCompleteItem.county,
                latitude = autoCompleteItem.latitude,
                longitude = autoCompleteItem.longitude
            ).flatMap { favorite: Favorite -> locationRepository.selectStoredLocation(storedLocation = favorite.toStoredLocation()) }
}