package fi.kroon.vadret.domain.warning

import fi.kroon.vadret.data.exception.Failure
import fi.kroon.vadret.data.feedsource.local.FeedSourceDao
import fi.kroon.vadret.data.functional.Either
import fi.kroon.vadret.util.FEED_SOURCE_MAX
import fi.kroon.vadret.util.extension.asRight
import io.reactivex.Single
import javax.inject.Inject

class CountFeedSourceEntityTask @Inject constructor(
    private val dao: FeedSourceDao
) {
    operator fun invoke(): Single<Either<Failure, Boolean>> =
        dao.count()
            .map { rowCount: Int ->
                (rowCount == FEED_SOURCE_MAX).asRight()
            }
}