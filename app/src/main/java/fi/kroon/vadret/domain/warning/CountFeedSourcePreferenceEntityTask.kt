package fi.kroon.vadret.domain.warning

import fi.kroon.vadret.data.failure.Failure
import fi.kroon.vadret.data.feedsourcepreference.local.FeedSourcePreferenceDao
import fi.kroon.vadret.util.APP_WARNING_FILTER_KEY
import fi.kroon.vadret.util.FEED_SOURCE_MAX
import fi.kroon.vadret.util.extension.asRight
import io.github.sphrak.either.Either
import io.reactivex.Single
import javax.inject.Inject
import timber.log.Timber

class CountFeedSourcePreferenceEntityTask @Inject constructor(
    private val dao: FeedSourcePreferenceDao
) {
    operator fun invoke(): Single<Either<Failure, Boolean>> =
        dao.count(usedBy = APP_WARNING_FILTER_KEY)
            .map { rowCount: Int ->
                Timber.d("ROW COUNT: $rowCount")
                (rowCount == FEED_SOURCE_MAX).asRight()
            }
}