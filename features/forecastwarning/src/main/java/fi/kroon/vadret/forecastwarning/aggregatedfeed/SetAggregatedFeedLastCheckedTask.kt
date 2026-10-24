package fi.kroon.vadret.forecastwarning.aggregatedfeed

import fi.kroon.vadret.data.aggregatedfeed.local.AggregatedFeedLocalDataSource
import fi.kroon.vadret.data.failure.Failure
import fi.kroon.vadret.util.APP_AGGREGATED_FEED_LAST_CHECKED_KEY
import io.github.sphrak.either.Either
import javax.inject.Inject

class SetAggregatedFeedLastCheckedTask @Inject constructor(
    private val local: AggregatedFeedLocalDataSource
) {
    suspend operator fun invoke(value: Long): Either<Failure, Unit> =
        local.putLong(key = APP_AGGREGATED_FEED_LAST_CHECKED_KEY, value = value)
}