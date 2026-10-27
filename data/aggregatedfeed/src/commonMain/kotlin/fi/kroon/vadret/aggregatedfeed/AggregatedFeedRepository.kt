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

    suspend fun getAggregatedFeed(
        feedSources: Set<FeedSource>,
        districtIds: Set<Int>,
        isRefresh: Boolean
    ): Either<Failure, List<AggregatedFeed>>
}

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