package fi.kroon.vadret.forecastwarning

import fi.kroon.vadret.data.failure.Failure
import fi.kroon.vadret.data.feedsourcepreference.local.FeedSourcePreferenceDao
import fi.kroon.vadret.util.APP_WARNING_FILTER_KEY
import fi.kroon.vadret.util.FEED_SOURCE_MAX
import fi.kroon.vadret.util.extension.asRight
import io.github.sphrak.either.Either
import timber.log.Timber
import javax.inject.Inject

class CountFeedSourcePreferenceEntityTask @Inject constructor(
    private val dao: FeedSourcePreferenceDao
) {
    suspend operator fun invoke(): Either<Failure, Boolean> =
        dao.count(usedBy = APP_WARNING_FILTER_KEY)
            .let { rowCount: Int ->
                Timber.d("ROW COUNT: $rowCount")
                (rowCount == FEED_SOURCE_MAX).asRight()
            }
}