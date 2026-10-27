package fi.kroon.vadret.weatherforecast

import fi.kroon.vadret.common.Failure
import fi.kroon.vadret.favorite.FavoriteRepository
import fi.kroon.vadret.favorite.model.Favorite
import fi.kroon.vadret.location.LocationRepository
import fi.kroon.vadret.location.model.LocationPreference
import fi.kroon.vadret.weatherforecast.extension.toLocationChoices
import fi.kroon.vadret.weatherforecast.model.LocationChoices
import io.github.sphrak.either.Either
import io.github.sphrak.either.flatMap
import io.github.sphrak.either.map
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine

internal class DefaultObserveLocationChoicesTask(
    private val favoriteRepository: FavoriteRepository,
    private val locationRepository: LocationRepository
) : ObserveLocationChoicesTask {

    override fun invoke(): Flow<Either<Failure, LocationChoices>> =
        combine(
            flow = favoriteRepository.observeFavorites(),
            flow2 = locationRepository.observeLocationPreference()
        ) { favorites: Either<Failure, List<Favorite>>, locationPreference: Either<Failure, LocationPreference> ->
            favorites.flatMap { favoriteList: List<Favorite> ->
                locationPreference.map { preference: LocationPreference -> preference.toLocationChoices(favorites = favoriteList) }
            }
        }
}