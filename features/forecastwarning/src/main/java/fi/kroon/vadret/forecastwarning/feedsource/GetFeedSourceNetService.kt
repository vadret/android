package fi.kroon.vadret.forecastwarning.feedsource

import fi.kroon.vadret.data.failure.Failure
import fi.kroon.vadret.data.feedsource.model.FeedSource
import fi.kroon.vadret.data.feedsource.model.FeedSourceEntity
import fi.kroon.vadret.forecastwarning.CountFeedSourceEntityTask
import io.github.sphrak.either.Either
import io.github.sphrak.either.flatMap
import io.github.sphrak.either.map
import timber.log.Timber
import javax.inject.Inject

class GetFeedSourceNetService @Inject constructor(
    private val countFeedSourceEntityTask: CountFeedSourceEntityTask,
    private val getFeedSourceNetTask: GetFeedSourceNetTask,
    private val setFeedSourceListTask: SetFeedSourceListTask
) {

    data class Data(
        val feedSourceList: List<FeedSource> = emptyList(),
        val feedSourceEntityList: List<FeedSourceEntity> = emptyList(),
        val feedSourceIsAvailable: Boolean = false
    )

    suspend operator fun invoke(): Either<Failure, Boolean> =
        getFeedSourceAvailabilityStatus(Data())
            .let { checkDistrictAvailability(it) }
            .let { setFeedSourceEntityList(it) }
            .let { transform(it) }

    private suspend fun getFeedSourceAvailabilityStatus(data: Data): Either<Failure, Data> =
        countFeedSourceEntityTask()
            .let { either: Either<Failure, Boolean> ->
                either.map { feedSourceIsAvailable: Boolean ->
                    data.copy(
                        feedSourceIsAvailable = feedSourceIsAvailable
                    )
                }
            }

    private suspend fun checkDistrictAvailability(either: Either<Failure, Data>): Either<Failure, Data> =
        either.flatMap { data: Data ->
            when (data.feedSourceIsAvailable) {
                false -> getFeedSourceNet(either)
                true -> either
            }
        }

    private suspend fun getFeedSourceNet(either: Either<Failure, Data>): Either<Failure, Data> =
        either.flatMap { data: Data ->
            getFeedSourceNetTask()
                .let { result: Either<Failure, List<FeedSource>> ->
                    result.map { feedSourceList: List<FeedSource> ->
                        Timber.d("GET DISTRICT VIEW NET")
                        feedSourceList.forEach {
                            Timber.d("$it")
                        }
                        data.copy(feedSourceList = feedSourceList)
                    }
                }
        }

    private suspend fun setFeedSourceEntityList(either: Either<Failure, Data>): Either<Failure, Data> =
        either.flatMap { data: Data ->
            setFeedSourceListTask(data.feedSourceList)
                .let { result: Either<Failure, List<Long>> ->
                    result.map { ids: List<Long> ->
                        Timber.d("IDS: $ids")
                        data.copy(feedSourceIsAvailable = true)
                    }
                }
        }

    private fun transform(either: Either<Failure, Data>): Either<Failure, Boolean> =
        either.map { data: Data ->
            data.feedSourceIsAvailable
        }
}