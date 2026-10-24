package fi.kroon.vadret.forecastwarning.warningfilter

import fi.kroon.vadret.data.failure.Failure
import fi.kroon.vadret.data.feedsource.model.FeedSourceOptionEntity
import fi.kroon.vadret.data.feedsourcepreference.local.FeedSourcePreferenceDao
import fi.kroon.vadret.util.APP_WARNING_FILTER_KEY
import fi.kroon.vadret.util.extension.asRight
import io.github.sphrak.either.Either
import javax.inject.Inject

class GetAppFeedSourceStateTask @Inject constructor(
    private val dao: FeedSourcePreferenceDao
) {
    /**
     *  Fetches currently enabled feed source state
     *  for [APP_WARNING_FILTER_KEY] from database.
     */
    suspend operator fun invoke(): Either<Failure, List<FeedSourceOptionEntity>> =
        dao.getAllUsedBy(usedBy = APP_WARNING_FILTER_KEY)
            .let { feedSourceOptionEntityList: List<FeedSourceOptionEntity> ->
                feedSourceOptionEntityList
                    .sortedWith(
                        compareBy { feedSourceOptionEntity: FeedSourceOptionEntity ->
                            feedSourceOptionEntity.name
                        }
                    ).asRight()
            }
}