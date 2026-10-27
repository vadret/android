package fi.kroon.vadret.weatherforecast

import fi.kroon.vadret.common.Failure
import fi.kroon.vadret.favorite.FavoriteRepository
import io.github.sphrak.either.Either

internal class DefaultRemoveFavoriteTask(
    private val favoriteRepository: FavoriteRepository
) : RemoveFavoriteTask {

    override suspend fun invoke(favoriteId: String): Either<Failure, Unit> = favoriteRepository.removeFavorite(favoriteId = favoriteId)
}