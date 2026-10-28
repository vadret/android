package fi.kroon.vadret.weatherforecast

import fi.kroon.vadret.common.Failure
import fi.kroon.vadret.place.model.Place
import io.github.sphrak.either.Either

internal interface SelectPlaceTask {

    suspend operator fun invoke(place: Place): Either<Failure, Unit>
}