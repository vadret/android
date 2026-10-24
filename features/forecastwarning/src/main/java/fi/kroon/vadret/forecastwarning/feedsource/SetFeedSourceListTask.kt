package fi.kroon.vadret.forecastwarning.feedsource

import fi.kroon.vadret.data.failure.Failure
import fi.kroon.vadret.data.feedsource.local.FeedSourceDao
import fi.kroon.vadret.data.feedsource.mapper.FeedSourceEntityMapper
import fi.kroon.vadret.data.feedsource.model.FeedSource
import fi.kroon.vadret.data.feedsource.model.FeedSourceEntity
import fi.kroon.vadret.util.extension.asRight
import io.github.sphrak.either.Either
import javax.inject.Inject

class SetFeedSourceListTask @Inject constructor(
    private val localDao: FeedSourceDao
) {
    suspend operator fun invoke(feedSourceList: List<FeedSource>): Either<Failure, List<Long>> {
        val feedSourceEntityList: List<FeedSourceEntity> = FeedSourceEntityMapper(feedSourceList)
        return localDao
            .insert(entityList = feedSourceEntityList)
            .let { ids: List<Long> ->
                ids.asRight()
            }
    }
}