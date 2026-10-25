package fi.kroon.vadret.weatherforecast

import fi.kroon.vadret.common.Failure
import io.github.sphrak.either.Either

internal interface RemoveFavoriteTask {

    suspend operator fun invoke(favoriteId: String): Either<Failure, Unit>
}