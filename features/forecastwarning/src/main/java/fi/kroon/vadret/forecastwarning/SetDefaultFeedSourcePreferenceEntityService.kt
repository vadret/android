package fi.kroon.vadret.forecastwarning

import fi.kroon.vadret.data.failure.Failure
import fi.kroon.vadret.data.feedsource.model.FeedSourceEntity
import fi.kroon.vadret.data.feedsourcepreference.model.FeedSourcePreferenceEntity
import fi.kroon.vadret.forecastwarning.feedsource.GetAllFeedSourceTask
import fi.kroon.vadret.forecastwarning.feedsourcepreference.SetFeedSourcePreferenceListTask
import fi.kroon.vadret.util.APP_WARNING_FILTER_KEY
import fi.kroon.vadret.util.FEED_SOURCE_KRISINFORMATION_ID
import fi.kroon.vadret.util.FEED_SOURCE_SMHI_ID
import io.github.sphrak.either.Either
import io.github.sphrak.either.flatMap
import io.github.sphrak.either.map
import timber.log.Timber
import javax.inject.Inject

class SetDefaultFeedSourcePreferenceEntityService @Inject constructor(
    private val setFeedSourcePreferenceListTask: SetFeedSourcePreferenceListTask,
    private val getAllFeedSourceTask: GetAllFeedSourceTask
) {

    private companion object {
        const val DEFAULT_FEEDSOURCE_ID_0 = FEED_SOURCE_KRISINFORMATION_ID
        const val DEFAULT_FEEDSOURCE_ID_1 = FEED_SOURCE_SMHI_ID
    }

    data class Data(
        val feedSourcePreferenceList: MutableList<FeedSourcePreferenceEntity> = mutableListOf()
    )

    suspend operator fun invoke(): Either<Failure, Boolean> =
        buildFeedSourcePreferenceList(Data())
            .let { setFeedSourcePreference(it) }

    private suspend fun buildFeedSourcePreferenceList(data: Data): Either<Failure, Data> =
        getAllFeedSourceTask()
            .let { result: Either<Failure, List<FeedSourceEntity>> ->
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

    private suspend fun setFeedSourcePreference(either: Either<Failure, Data>): Either<Failure, Boolean> =
        either.flatMap { data: Data ->
            setFeedSourcePreferenceListTask(entityList = data.feedSourcePreferenceList)
                .let { result: Either<Failure, List<Long>> ->
                    result.map { ids: List<Long> ->
                        Timber.d("INSERTED FEED: $ids")
                        true
                    }
                }
        }
}