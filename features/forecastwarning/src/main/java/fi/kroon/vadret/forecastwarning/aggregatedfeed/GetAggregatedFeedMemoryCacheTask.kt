package fi.kroon.vadret.forecastwarning.aggregatedfeed

import fi.kroon.vadret.data.aggregatedfeed.cache.AggregatedFeedCacheDataSource
import fi.kroon.vadret.data.aggregatedfeed.model.AggregatedFeed
import fi.kroon.vadret.data.failure.Failure
import io.reactivex.Single
import se.fimbla.either.Either
import javax.inject.Inject

class GetAggregatedFeedMemoryCacheTask @Inject constructor(
    private val repo: AggregatedFeedCacheDataSource
) {
    operator fun invoke(): Single<Either<Failure, List<AggregatedFeed>>> =
        repo
            .getMemoryCache()
}