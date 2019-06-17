package fi.kroon.vadret.domain.aggregatedfeed

import fi.kroon.vadret.data.aggregatedfeed.local.AggregatedFeedLocalDataSource
import fi.kroon.vadret.data.exception.Failure
import fi.kroon.vadret.data.functional.Either
import fi.kroon.vadret.util.APP_AGGREGATED_FEED_LAST_CHECKED_KEY
import io.reactivex.Single
import javax.inject.Inject

class SetAggregatedFeedLastCheckedTask @Inject constructor(
    private val local: AggregatedFeedLocalDataSource
) {
    operator fun invoke(value: Long): Single<Either<Failure, Unit>> =
        local.putLong(key = APP_AGGREGATED_FEED_LAST_CHECKED_KEY, value = value)
}