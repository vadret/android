package fi.kroon.vadret.place

import fi.kroon.vadret.common.Failure
import fi.kroon.vadret.place.local.DefaultPlaceLocalDataSource
import fi.kroon.vadret.place.model.Place
import io.github.sphrak.either.Either
import kotlinx.coroutines.CoroutineDispatcher

interface PlaceRepository {

    suspend fun search(
        prefix: String,
        limit: Int
    ): Either<Failure, List<Place>>
}

fun PlaceRepository(
    readPlaces: suspend () -> String,
    coroutineDispatcher: CoroutineDispatcher
): PlaceRepository =
    DefaultPlaceRepository(
        placeLocalDataSource = DefaultPlaceLocalDataSource(
            readPlaces = readPlaces,
            coroutineDispatcher = coroutineDispatcher
        )
    )