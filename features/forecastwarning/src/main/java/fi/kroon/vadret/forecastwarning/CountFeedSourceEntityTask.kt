package fi.kroon.vadret.forecastwarning

import fi.kroon.vadret.data.failure.Failure
import fi.kroon.vadret.data.feedsource.local.FeedSourceDao
import fi.kroon.vadret.util.FEED_SOURCE_MAX
import fi.kroon.vadret.util.extension.asRight
import io.github.sphrak.either.Either
import javax.inject.Inject

class CountFeedSourceEntityTask @Inject constructor(
    private val dao: FeedSourceDao
) {
    suspend operator fun invoke(): Either<Failure, Boolean> =
        dao.count()
            .let { rowCount: Int ->
                (rowCount == FEED_SOURCE_MAX).asRight()
            }
}