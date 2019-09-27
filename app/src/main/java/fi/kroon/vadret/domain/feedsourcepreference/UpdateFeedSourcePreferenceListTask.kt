package fi.kroon.vadret.domain.feedsourcepreference

import fi.kroon.vadret.data.failure.Failure
import fi.kroon.vadret.data.feedsource.model.FeedSourceOptionEntity
import fi.kroon.vadret.data.feedsourcepreference.local.FeedSourcePreferenceDao
import fi.kroon.vadret.data.feedsourcepreference.model.FeedSourcePreferenceEntity
import fi.kroon.vadret.util.extension.asRight
import io.github.sphrak.either.Either
import io.reactivex.Single
import javax.inject.Inject
import timber.log.Timber

class UpdateFeedSourcePreferenceListTask @Inject constructor(
    private val dao: FeedSourcePreferenceDao
) {
    operator fun invoke(entityList: List<FeedSourceOptionEntity>): Single<Either<Failure, Int>> {

        val newEntityList: List<FeedSourcePreferenceEntity> =
            entityList.map { feedSourceOptionEntity: FeedSourceOptionEntity ->
                with(feedSourceOptionEntity) {
                    FeedSourcePreferenceEntity(
                        id = feedSourceId,
                        feedSourceId = id,
                        usedBy = usedBy,
                        isEnabled = isEnabled
                    )
                }
            }
        Timber.d("NEW ENTITY LIST: $newEntityList")

        return dao.update(entityList = newEntityList)
            .map { updatedRowCount: Int ->
                Timber.d("updated row count: $updatedRowCount")
                updatedRowCount.asRight()
            }
    }
}