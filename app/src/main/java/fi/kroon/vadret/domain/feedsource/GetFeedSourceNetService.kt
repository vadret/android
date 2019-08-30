package fi.kroon.vadret.domain.feedsource

import fi.kroon.vadret.data.exception.Failure
import fi.kroon.vadret.data.feedsource.model.FeedSource
import fi.kroon.vadret.data.feedsource.model.FeedSourceEntity
import fi.kroon.vadret.domain.warning.CountFeedSourceEntityTask
import fi.kroon.vadret.util.extension.asSingle
import fi.kroon.vadret.util.extension.flatMapSingle
import io.github.sphrak.either.Either
import io.github.sphrak.either.map
import io.reactivex.Single
import javax.inject.Inject
import timber.log.Timber

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

    operator fun invoke(): Single<Either<Failure, Boolean>> =
        Single.just(Data())
            .flatMap(::getFeedSourceAvailabilityStatus)
            .flatMap(::checkDistrictAvailability)
            .flatMap(::setFeedSourceEntityList)
            .map(::transform)

    private fun getFeedSourceAvailabilityStatus(data: Data): Single<Either<Failure, Data>> =
        countFeedSourceEntityTask()
            .map { either: Either<Failure, Boolean> ->
                either.map { feedSourceIsAvailable: Boolean ->
                    data.copy(
                        feedSourceIsAvailable = feedSourceIsAvailable
                    )
                }
            }

    private fun checkDistrictAvailability(either: Either<Failure, Data>): Single<Either<Failure, Data>> =
        either.flatMapSingle { data: Data ->
            when (data.feedSourceIsAvailable) {
                false -> getFeedSourceNet(either)
                true -> either.asSingle()
            }
        }

    private fun getFeedSourceNet(either: Either<Failure, Data>): Single<Either<Failure, Data>> =
        either.flatMapSingle { data: Data ->
            getFeedSourceNetTask()
                .map { result: Either<Failure, List<FeedSource>> ->
                    result.map { feedSourceList: List<FeedSource> ->
                        Timber.d("GET DISTRICT VIEW NET")
                        feedSourceList.forEach {
                            Timber.d("$it")
                        }
                        data.copy(feedSourceList = feedSourceList)
                    }
                }
        }

    private fun setFeedSourceEntityList(either: Either<Failure, Data>): Single<Either<Failure, Data>> =
        either.flatMapSingle { data: Data ->
            setFeedSourceListTask(data.feedSourceList)
                .map { result: Either<Failure, List<Long>> ->
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