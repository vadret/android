package fi.kroon.vadret.wind

import fi.kroon.vadret.common.Failure
import fi.kroon.vadret.wind.local.DefaultWindLocalDataSource
import fi.kroon.vadret.wind.model.WindForecast
import fi.kroon.vadret.wind.remote.DefaultWindRemoteDataSource
import io.github.sphrak.either.Either
import io.ktor.client.HttpClient
import kotlin.time.Clock

interface WindRepository {

    suspend fun getWindForecast(): Either<Failure, WindForecast>
}

fun WindRepository(
    httpClient: Lazy<HttpClient>,
    clock: Clock
): WindRepository =
    DefaultWindRepository(
        windRemoteDataSource = DefaultWindRemoteDataSource(httpClient = httpClient),
        windLocalDataSource = DefaultWindLocalDataSource(),
        clock = clock
    )