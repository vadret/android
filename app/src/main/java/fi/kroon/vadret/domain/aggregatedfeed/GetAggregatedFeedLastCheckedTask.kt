package fi.kroon.vadret.domain.aggregatedfeed

import fi.kroon.vadret.data.aggregatedfeed.local.AggregatedFeedLocalDataSource
import fi.kroon.vadret.data.exception.Failure
import fi.kroon.vadret.util.APP_AGGREGATED_FEED_LAST_CHECKED_KEY
import io.github.sphrak.either.Either
import io.reactivex.Single
import javax.inject.Inject

class GetAggregatedFeedLastCheckedTask @Inject constructor(
    private val local: AggregatedFeedLocalDataSource
) {
    operator fun invoke(): Single<Either<Failure, Long>> =
        local
            .getLong(key = APP_AGGREGATED_FEED_LAST_CHECKED_KEY)
}