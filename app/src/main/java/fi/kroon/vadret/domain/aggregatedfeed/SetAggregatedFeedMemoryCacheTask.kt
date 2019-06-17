package fi.kroon.vadret.domain.aggregatedfeed

import fi.kroon.vadret.data.aggregatedfeed.cache.AggregatedFeedCacheDataSource
import fi.kroon.vadret.data.aggregatedfeed.model.AggregatedFeed
import fi.kroon.vadret.data.exception.Failure
import fi.kroon.vadret.data.functional.Either
import io.reactivex.Single
import javax.inject.Inject

class SetAggregatedFeedMemoryCacheTask @Inject constructor(
    private val repo: AggregatedFeedCacheDataSource
) {
    operator fun invoke(aggregatedFeedList: List<AggregatedFeed>): Single<Either<Failure, List<AggregatedFeed>>> =
        repo
            .updateMemoryCache(aggregatedFeedList)
}