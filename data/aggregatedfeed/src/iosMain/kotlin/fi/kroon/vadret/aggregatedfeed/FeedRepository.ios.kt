package fi.kroon.vadret.aggregatedfeed

import androidx.room3.Room
import androidx.sqlite.driver.NativeSQLiteDriver
import fi.kroon.vadret.aggregatedfeed.local.AggregatedFeedDatabase
import fi.kroon.vadret.common.DiskCache
import io.ktor.client.HttpClient
import kotlinx.coroutines.CoroutineDispatcher
import kotlin.time.Clock

fun FeedRepository(
    databaseDirectory: String,
    httpClient: Lazy<HttpClient>,
    diskCache: DiskCache,
    clock: Clock,
    coroutineDispatcher: CoroutineDispatcher
): FeedRepository =
    FeedRepository(
        httpClient = httpClient,
        diskCache = diskCache,
        databaseBuilder = Room
            .databaseBuilder<AggregatedFeedDatabase>(name = "$databaseDirectory/$AGGREGATED_FEED_DATABASE_NAME")
            .setDriver(driver = NativeSQLiteDriver())
            .setQueryCoroutineContext(context = coroutineDispatcher),
        clock = clock,
        coroutineDispatcher = coroutineDispatcher
    )