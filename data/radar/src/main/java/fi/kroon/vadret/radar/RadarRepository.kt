package fi.kroon.vadret.radar

import fi.kroon.vadret.common.DiskCache
import fi.kroon.vadret.common.Failure
import fi.kroon.vadret.radar.local.DefaultRadarLocalDataSource
import fi.kroon.vadret.radar.model.Radar
import fi.kroon.vadret.radar.remote.DefaultRadarRemoteDataSource
import io.github.sphrak.either.Either
import io.ktor.client.HttpClient
import kotlinx.coroutines.CoroutineDispatcher
import kotlin.time.Clock

interface RadarRepository {

    /**
     *  Today's radar images, from the cache when they were
     *  fetched less than fifteen minutes ago.
     */
    suspend fun getRadar(): Either<Failure, Radar>
}

/**
 *  [httpClient] is only created on first use, never on the main thread.
 */
fun RadarRepository(
    httpClient: Lazy<HttpClient>,
    diskCache: DiskCache,
    clock: Clock,
    coroutineDispatcher: CoroutineDispatcher
): RadarRepository =
    DefaultRadarRepository(
        radarRemoteDataSource = DefaultRadarRemoteDataSource(httpClient = httpClient),
        radarLocalDataSource = DefaultRadarLocalDataSource(
            diskCache = diskCache,
            coroutineDispatcher = coroutineDispatcher
        ),
        clock = clock
    )