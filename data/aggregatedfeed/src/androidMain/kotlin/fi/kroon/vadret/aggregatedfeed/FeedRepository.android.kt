package fi.kroon.vadret.aggregatedfeed

import android.content.Context
import androidx.room3.Room
import fi.kroon.vadret.aggregatedfeed.local.AggregatedFeedDatabase
import fi.kroon.vadret.common.DiskCache
import io.ktor.client.HttpClient
import kotlinx.coroutines.CoroutineDispatcher
import kotlin.time.Clock

fun FeedRepository(
    context: Context,
    httpClient: Lazy<HttpClient>,
    diskCache: DiskCache,
    clock: Clock,
    coroutineDispatcher: CoroutineDispatcher
): FeedRepository =
    FeedRepository(
        httpClient = httpClient,
        diskCache = diskCache,
        databaseBuilder = Room.databaseBuilder<AggregatedFeedDatabase>(
            context = context,
            name = AGGREGATED_FEED_DATABASE_NAME
        ),
        clock = clock,
        coroutineDispatcher = coroutineDispatcher
    )