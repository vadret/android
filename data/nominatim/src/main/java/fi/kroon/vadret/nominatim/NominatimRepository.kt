package fi.kroon.vadret.nominatim

import fi.kroon.vadret.common.Failure
import fi.kroon.vadret.nominatim.model.Locality
import fi.kroon.vadret.nominatim.remote.DefaultNominatimRemoteDataSource
import io.github.sphrak.either.Either
import io.ktor.client.HttpClient

interface NominatimRepository {

    suspend fun getLocality(
        latitude: Double,
        longitude: Double
    ): Either<Failure, Locality>
}

fun NominatimRepository(httpClient: Lazy<HttpClient>): NominatimRepository =
    DefaultNominatimRepository(
        nominatimRemoteDataSource = DefaultNominatimRemoteDataSource(httpClient = httpClient)
    )