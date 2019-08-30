package fi.kroon.vadret.data.aggregatedfeed.local

import com.afollestad.rxkprefs.RxkPrefs
import fi.kroon.vadret.data.aggregatedfeed.exception.AggregatedFeedFailure
import fi.kroon.vadret.data.exception.Failure
import fi.kroon.vadret.util.APP_AGGREGATED_FEED_LAST_CHECKED_KEY
import fi.kroon.vadret.util.extension.asLeft
import fi.kroon.vadret.util.extension.asRight
import fi.kroon.vadret.util.extension.asSingle
import io.github.sphrak.either.Either
import io.reactivex.Single
import javax.inject.Inject

class AggregatedFeedLocalDataSource @Inject constructor(
    private val prefs: RxkPrefs
) {
    fun putLong(key: String, value: Long): Single<Either<Failure, Unit>> =
        when (key) {
            APP_AGGREGATED_FEED_LAST_CHECKED_KEY -> {
                prefs.long(key = APP_AGGREGATED_FEED_LAST_CHECKED_KEY)
                    .set(value = value)
                    .asRight()
                    .asSingle()
            }
            else -> {
                AggregatedFeedFailure
                    .SavingPreferenceFailed
                    .asLeft()
                    .asSingle()
            }
        }

    fun getLong(key: String): Single<Either<Failure, Long>> =
        when (key) {
            APP_AGGREGATED_FEED_LAST_CHECKED_KEY -> {
                prefs.long(key = APP_AGGREGATED_FEED_LAST_CHECKED_KEY)
                    .get()
                    .asRight()
                    .asSingle()
            }
            else -> {
                AggregatedFeedFailure
                    .LoadingPreferenceFailed
                    .asLeft()
                    .asSingle()
            }
        }
}