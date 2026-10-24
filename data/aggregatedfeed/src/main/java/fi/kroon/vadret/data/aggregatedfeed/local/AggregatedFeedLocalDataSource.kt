package fi.kroon.vadret.data.aggregatedfeed.local

import fi.kroon.vadret.data.aggregatedfeed.exception.AggregatedFeedFailure
import fi.kroon.vadret.data.failure.Failure
import fi.kroon.vadret.data.preference.Prefs
import fi.kroon.vadret.util.APP_AGGREGATED_FEED_LAST_CHECKED_KEY
import fi.kroon.vadret.util.extension.asLeft
import fi.kroon.vadret.util.extension.asRight
import io.github.sphrak.either.Either
import javax.inject.Inject

class AggregatedFeedLocalDataSource @Inject constructor(
    private val prefs: Prefs
) {
    fun putLong(key: String, value: Long): Either<Failure, Unit> =
        when (key) {
            APP_AGGREGATED_FEED_LAST_CHECKED_KEY -> {
                prefs.long(key = APP_AGGREGATED_FEED_LAST_CHECKED_KEY)
                    .set(value = value)
                    .asRight()
            }
            else -> {
                AggregatedFeedFailure
                    .SavingPreferenceFailed
                    .asLeft()
            }
        }

    fun getLong(key: String): Either<Failure, Long> =
        when (key) {
            APP_AGGREGATED_FEED_LAST_CHECKED_KEY -> {
                prefs.long(key = APP_AGGREGATED_FEED_LAST_CHECKED_KEY)
                    .get()
                    .asRight()
            }
            else -> {
                AggregatedFeedFailure
                    .LoadingPreferenceFailed
                    .asLeft()
            }
        }
}