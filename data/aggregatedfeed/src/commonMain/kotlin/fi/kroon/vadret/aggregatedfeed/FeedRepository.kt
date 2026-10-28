package fi.kroon.vadret.aggregatedfeed

import androidx.room3.RoomDatabase
import fi.kroon.vadret.aggregatedfeed.local.AggregatedFeedDatabase
import fi.kroon.vadret.aggregatedfeed.local.DefaultAggregatedFeedLocalDataSource
import fi.kroon.vadret.aggregatedfeed.local.DefaultSeenWarningLocalDataSource
import fi.kroon.vadret.aggregatedfeed.model.AggregatedFeed
import fi.kroon.vadret.aggregatedfeed.model.SeenWarning
import fi.kroon.vadret.aggregatedfeed.remote.DefaultKrisinformationRemoteDataSource
import fi.kroon.vadret.aggregatedfeed.remote.DefaultSmhiWarningRemoteDataSource
import fi.kroon.vadret.common.DiskCache
import fi.kroon.vadret.common.Failure
import io.github.sphrak.either.Either
import io.ktor.client.HttpClient
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlin.time.Clock

internal const val AGGREGATED_FEED_DATABASE_NAME = "aggregatedfeed.db"

interface FeedRepository {

    suspend fun getAggregatedFeed(isRefresh: Boolean): Either<Failure, List<AggregatedFeed>>

    fun observeSeenWarnings(): Flow<Either<Failure, List<SeenWarning>>>

    suspend fun markSeen(warningIdentifier: String): Either<Failure, Unit>
}

internal fun FeedRepository(
    httpClient: Lazy<HttpClient>,
    diskCache: DiskCache,
    databaseBuilder: RoomDatabase.Builder<AggregatedFeedDatabase>,
    clock: Clock,
    coroutineDispatcher: CoroutineDispatcher
): FeedRepository =
    DefaultFeedRepository(
        smhiWarningRemoteDataSource = DefaultSmhiWarningRemoteDataSource(httpClient = httpClient),
        krisinformationRemoteDataSource = DefaultKrisinformationRemoteDataSource(httpClient = httpClient),
        aggregatedFeedLocalDataSource = DefaultAggregatedFeedLocalDataSource(
            diskCache = diskCache,
            coroutineDispatcher = coroutineDispatcher
        ),
        seenWarningLocalDataSource = DefaultSeenWarningLocalDataSource(aggregatedFeedDatabase = databaseBuilder.build()),
        clock = clock
    )