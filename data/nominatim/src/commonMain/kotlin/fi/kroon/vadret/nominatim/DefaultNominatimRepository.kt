package fi.kroon.vadret.nominatim

import fi.kroon.vadret.common.Failure
import fi.kroon.vadret.common.catch
import fi.kroon.vadret.common.either
import fi.kroon.vadret.nominatim.extension.toFailure
import fi.kroon.vadret.nominatim.extension.toLocality
import fi.kroon.vadret.nominatim.model.Locality
import fi.kroon.vadret.nominatim.remote.NominatimRemoteDataSource
import io.github.sphrak.either.Either

internal class DefaultNominatimRepository(
    private val nominatimRemoteDataSource: NominatimRemoteDataSource
) : NominatimRepository {

    override suspend fun getLocality(
        latitude: Double,
        longitude: Double
    ): Either<Failure, Locality> =
        either {
            nominatimRemoteDataSource
                .reverse(
                    latitude = latitude,
                    longitude = longitude
                ).toLocality()
        }.catch { exception: Exception -> exception.toFailure() }
}