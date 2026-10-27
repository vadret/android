package fi.kroon.vadret.weatherforecast

import fi.kroon.vadret.common.Failure
import fi.kroon.vadret.favorite.model.Favorite
import io.github.sphrak.either.Either

internal interface SelectFavoriteTask {

    suspend operator fun invoke(favorite: Favorite): Either<Failure, Unit>
}