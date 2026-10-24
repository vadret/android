package fi.kroon.vadret.forecastwarning.aggregatedfeed

import fi.kroon.vadret.data.aggregatedfeed.cache.AggregatedFeedCacheDataSource
import fi.kroon.vadret.data.aggregatedfeed.model.AggregatedFeed
import fi.kroon.vadret.data.failure.Failure
import io.github.sphrak.either.Either
import javax.inject.Inject

class SetAggregatedFeedDiskCacheTask @Inject constructor(
    private val repo: AggregatedFeedCacheDataSource
) {
    suspend operator fun invoke(aggregatedFeedList: List<AggregatedFeed>): Either<Failure, List<AggregatedFeed>> =
        repo
            .updateDiskCache(aggregatedFeedList)
}