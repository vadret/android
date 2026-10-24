package fi.kroon.vadret.aggregatedfeed

import fi.kroon.vadret.aggregatedfeed.local.DefaultAggregatedFeedLocalDataSource
import fi.kroon.vadret.aggregatedfeed.model.AggregatedFeed
import fi.kroon.vadret.aggregatedfeed.model.FeedSource
import fi.kroon.vadret.aggregatedfeed.remote.DefaultKrisinformationRemoteDataSource
import fi.kroon.vadret.aggregatedfeed.remote.DefaultSmhiWarningRemoteDataSource
import fi.kroon.vadret.common.DiskCache
import fi.kroon.vadret.common.Failure
import io.github.sphrak.either.Either
import io.ktor.client.HttpClient
import kotlinx.coroutines.CoroutineDispatcher
import kotlin.time.Clock

interface AggregatedFeedRepository {

    /**
     *  SMHI warnings affecting [districtIds] together with Krisinformation news
     *  and VMAs, from the enabled [feedSources] and newest first. The feed comes
     *  from the cache when it was fetched for the same sources and districts in
     *  the last five minutes, unless [isRefresh] asks for a new one.
     */
    suspend fun getAggregatedFeed(
        feedSources: Set<FeedSource>,
        districtIds: Set<Int>,
        isRefresh: Boolean
    ): Either<Failure, List<AggregatedFeed>>
}

/**
 *  [httpClient] is only created on first use, never on the main thread.
 */
fun AggregatedFeedRepository(
    httpClient: Lazy<HttpClient>,
    diskCache: DiskCache,
    clock: Clock,
    coroutineDispatcher: CoroutineDispatcher
): AggregatedFeedRepository =
    DefaultAggregatedFeedRepository(
        smhiWarningRemoteDataSource = DefaultSmhiWarningRemoteDataSource(httpClient = httpClient),
        krisinformationRemoteDataSource = DefaultKrisinformationRemoteDataSource(httpClient = httpClient),
        aggregatedFeedLocalDataSource = DefaultAggregatedFeedLocalDataSource(
            diskCache = diskCache,
            coroutineDispatcher = coroutineDispatcher
        ),
        clock = clock
    )