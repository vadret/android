package fi.kroon.vadret.place

import fi.kroon.vadret.common.Failure
import fi.kroon.vadret.common.catch
import fi.kroon.vadret.common.either
import fi.kroon.vadret.place.extension.toFailure
import fi.kroon.vadret.place.extension.toPlace
import fi.kroon.vadret.place.local.PlaceEntity
import fi.kroon.vadret.place.local.PlaceLocalDataSource
import fi.kroon.vadret.place.model.Place
import io.github.sphrak.either.Either

internal class DefaultPlaceRepository(
    private val placeLocalDataSource: PlaceLocalDataSource
) : PlaceRepository {

    override suspend fun search(
        prefix: String,
        limit: Int
    ): Either<Failure, List<Place>> =
        either {
            placeLocalDataSource
                .search(
                    prefix = prefix,
                    limit = limit
                ).map { placeEntity: PlaceEntity -> placeEntity.toPlace() }
        }.catch { exception: Exception -> exception.toFailure() }
}