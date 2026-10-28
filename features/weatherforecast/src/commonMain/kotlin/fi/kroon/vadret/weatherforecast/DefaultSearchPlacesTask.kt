package fi.kroon.vadret.weatherforecast

import fi.kroon.vadret.common.Failure
import fi.kroon.vadret.place.PlaceRepository
import fi.kroon.vadret.place.model.Place
import io.github.sphrak.either.Either

private const val SUGGESTION_LIMIT = 10

internal class DefaultSearchPlacesTask(
    private val placeRepository: PlaceRepository
) : SearchPlacesTask {

    override suspend fun invoke(prefix: String): Either<Failure, List<Place>> =
        placeRepository.search(
            prefix = prefix,
            limit = SUGGESTION_LIMIT
        )
}