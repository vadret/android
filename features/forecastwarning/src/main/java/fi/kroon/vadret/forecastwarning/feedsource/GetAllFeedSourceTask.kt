package fi.kroon.vadret.forecastwarning.feedsource

import fi.kroon.vadret.data.failure.Failure
import fi.kroon.vadret.data.feedsource.local.FeedSourceDao
import fi.kroon.vadret.data.feedsource.model.FeedSourceEntity
import fi.kroon.vadret.util.extension.asRight
import io.github.sphrak.either.Either
import javax.inject.Inject

class GetAllFeedSourceTask @Inject constructor(
    private val dao: FeedSourceDao
) {
    suspend operator fun invoke(): Either<Failure, List<FeedSourceEntity>> =
        dao.getAll()
            .let { feedSourceList: List<FeedSourceEntity> ->
                feedSourceList.asRight()
            }
}