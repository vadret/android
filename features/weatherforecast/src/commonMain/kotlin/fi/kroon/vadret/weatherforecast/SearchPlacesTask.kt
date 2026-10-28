package fi.kroon.vadret.weatherforecast

import fi.kroon.vadret.common.Failure
import fi.kroon.vadret.place.model.Place
import io.github.sphrak.either.Either

internal interface SearchPlacesTask {

    suspend operator fun invoke(prefix: String): Either<Failure, List<Place>>
}