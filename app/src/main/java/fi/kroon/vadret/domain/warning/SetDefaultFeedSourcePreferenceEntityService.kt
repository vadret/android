package fi.kroon.vadret.domain.warning

import fi.kroon.vadret.data.failure.Failure
import fi.kroon.vadret.data.feedsource.model.FeedSourceEntity
import fi.kroon.vadret.data.feedsourcepreference.model.FeedSourcePreferenceEntity
import fi.kroon.vadret.domain.feedsource.GetAllFeedSourceTask
import fi.kroon.vadret.domain.feedsourcepreference.SetFeedSourcePreferenceListTask
import fi.kroon.vadret.util.APP_WARNING_FILTER_KEY
import fi.kroon.vadret.util.extension.flatMapSingle
import io.github.sphrak.either.Either
import io.github.sphrak.either.map
import io.reactivex.Single
import timber.log.Timber
import javax.inject.Inject

class SetDefaultFeedSourcePreferenceEntityService @Inject constructor(
    private val setFeedSourcePreferenceListTask: SetFeedSourcePreferenceListTask,
    private val getAllFeedSourceTask: GetAllFeedSourceTask
) {

    private companion object {
        const val DEFAULT_FEEDSOURCE_ID_0 = 1
        const val DEFAULT_FEEDSOURCE_ID_1 = 2
    }

    data class Data(
        val feedSourcePreferenceList: MutableList<FeedSourcePreferenceEntity> = mutableListOf()
    )

    operator fun invoke(): Single<Either<Failure, Boolean>> =
        Single.just(Data())
            .flatMap(::buildFeedSourcePreferenceList)
            .flatMap(::setFeedSourcePreference)

    private fun buildFeedSourcePreferenceList(data: Data): Single<Either<Failure, Data>> =
        getAllFeedSourceTask()
            .map { result: Either<Failure, List<FeedSourceEntity>> ->
                result.map { ids: List<FeedSourceEntity> ->
                    ids.forEach { feedSourceEntity: FeedSourceEntity ->

                        Timber.d("ITER: ${feedSourceEntity.id}")
                        val isEnabled: Boolean = feedSourceEntity.id == DEFAULT_FEEDSOURCE_ID_0 || feedSourceEntity.id == DEFAULT_FEEDSOURCE_ID_1
                        val feedSourcePreferenceEntity =
                            FeedSourcePreferenceEntity(
                                feedSourceId = feedSourceEntity.id,
                                usedBy = APP_WARNING_FILTER_KEY,
                                isEnabled = isEnabled
                            )
                        data.feedSourcePreferenceList.add(feedSourcePreferenceEntity)
                    }
                    Timber.d("FEED SOURCE LIST TO SAVE: ${data.feedSourcePreferenceList}")
                    data
                }
            }

    private fun setFeedSourcePreference(either: Either<Failure, Data>): Single<Either<Failure, Boolean>> =
        either.flatMapSingle { data: Data ->
            setFeedSourcePreferenceListTask(entityList = data.feedSourcePreferenceList)
                .map { result: Either<Failure, List<Long>> ->
                    result.map { ids: List<Long> ->
                        Timber.d("INSERTED FEED: $ids")
                        true
                    }
                }
        }
}