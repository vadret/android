package fi.kroon.vadret.forecastwarning.aggregatedfeed

import fi.kroon.vadret.data.aggregatedfeed.local.AggregatedFeedLocalDataSource
import fi.kroon.vadret.data.failure.Failure
import fi.kroon.vadret.util.APP_AGGREGATED_FEED_LAST_CHECKED_KEY
import io.github.sphrak.either.Either
import javax.inject.Inject

class GetAggregatedFeedLastCheckedTask @Inject constructor(
    private val local: AggregatedFeedLocalDataSource
) {
    suspend operator fun invoke(): Either<Failure, Long> =
        local
            .getLong(key = APP_AGGREGATED_FEED_LAST_CHECKED_KEY)
}