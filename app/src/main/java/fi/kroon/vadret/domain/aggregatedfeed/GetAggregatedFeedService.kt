package fi.kroon.vadret.domain.aggregatedfeed

import fi.kroon.vadret.data.aggregatedfeed.model.AggregatedFeed
import fi.kroon.vadret.data.exception.Failure
import fi.kroon.vadret.data.functional.Either
import fi.kroon.vadret.data.functional.flatMap
import fi.kroon.vadret.data.functional.flatMapSingle
import fi.kroon.vadret.data.functional.map
import fi.kroon.vadret.domain.IService
import fi.kroon.vadret.util.FIVE_MINUTES_IN_MILLIS
import io.reactivex.Single
import io.reactivex.rxkotlin.zipWith
import javax.inject.Inject
import timber.log.Timber

class GetAggregatedFeedService @Inject constructor(
    private val getAggregatedFeedTask: GetAggregatedFeedTask,
    private val getAggregatedFeedDiskCacheTask: GetAggregatedFeedDiskCacheTask,
    private val getAggregatedFeedMemoryCacheTask: GetAggregatedFeedMemoryCacheTask,
    private val setAggregatedFeedDiskCacheTask: SetAggregatedFeedDiskCacheTask,
    private val setAggregatedFeedMemoryCacheTask: SetAggregatedFeedMemoryCacheTask
) : IService {

    data class Data(
        val aggregatedFeedList: List<AggregatedFeed> = emptyList(),
        val counties: List<Int>,
        val feeds: List<Int>,
        val forceNet: Boolean = false,
        val timeStamp: Long
    )

    operator fun invoke(timeStamp: Long, forceNet: Boolean, districtIds: List<Int>, feedSourceIds: List<Int>): Single<Either<Failure, Data>> =
        Single.just(
            Data(
                timeStamp = timeStamp,
                forceNet = forceNet,
                counties = districtIds,
                feeds = feedSourceIds
            )
        ).flatMap(::getAggregatedFeed)
            .map(::transform)

    private fun getAggregatedFeed(data: Data): Single<Either<Failure, Data>> =
        getAlert(data)
            .map { either: Either<Failure, Data> ->
                either.map { dataIn: Data ->
                    dataIn.copy(
                        timeStamp = currentTimeMillis
                    )
                }
            }

    private fun getAlert(data: Data): Single<Either<Failure, Data>> =
        with(data) {
            when {
                forceNet || (currentTimeMillis > (timeStamp + FIVE_MINUTES_IN_MILLIS)) -> {
                    getAggregatedFeedTask(counties = data.counties, feeds = data.feeds)
                        .map { either: Either<Failure, List<AggregatedFeed>> ->
                            either.map { aggregatedFeedList: List<AggregatedFeed> ->
                                data.copy(aggregatedFeedList = aggregatedFeedList)
                            }
                        }.flatMap { either: Either<Failure, Data> ->
                            updateCache(either)
                        }
                }
                else -> {
                    Single.merge(
                        getAggregatedFeedMemoryCacheTask()
                            .map { either: Either<Failure, List<AggregatedFeed>> ->
                                either.map { aggregatedFeedList: List<AggregatedFeed> ->
                                    data.copy(aggregatedFeedList = aggregatedFeedList)
                                }
                            },
                        getAggregatedFeedDiskCacheTask()
                            .map { either: Either<Failure, List<AggregatedFeed>> ->
                                either.map { aggregatedFeedList: List<AggregatedFeed> ->
                                    data.copy(aggregatedFeedList = aggregatedFeedList)
                                }
                            }
                    ).filter { result: Either<Failure, Data> ->
                        result.either(
                            {
                                false
                            },
                            { data: Data ->
                                Timber.d("Fetched from cache ${data.aggregatedFeedList}")
                                data
                                    .aggregatedFeedList
                                    .isNotEmpty()
                            }
                        )
                    }.take(1)
                        .switchIfEmpty(
                            getAggregatedFeedTask(counties = data.counties, feeds = data.feeds)
                                .map { either: Either<Failure, List<AggregatedFeed>> ->
                                    Timber.d("Cache was empty, fetching from network.")
                                    either.map { aggregatedFeedList: List<AggregatedFeed> ->
                                        data.copy(aggregatedFeedList = aggregatedFeedList)
                                    }
                                }.flatMap { data: Either<Failure, Data> ->
                                    updateCache(data)
                                }.toFlowable()
                        ).singleOrError()
                }
            }
        }

    private fun updateCache(either: Either<Failure, Data>): Single<Either<Failure, Data>> =
        either.flatMapSingle { data: Data ->
            setAggregatedFeedMemoryCacheTask(data.aggregatedFeedList)
                .zipWith(setAggregatedFeedDiskCacheTask(data.aggregatedFeedList))
                .map { pair: Pair<Either<Failure, List<AggregatedFeed>>, Either<Failure, List<AggregatedFeed>>> ->
                    Timber.i("Updating cache")
                    val (firstEither: Either<Failure, List<AggregatedFeed>>, secondEither: Either<Failure, List<AggregatedFeed>>) = pair
                    firstEither.flatMap { _: List<AggregatedFeed> ->
                        secondEither.map { _: List<AggregatedFeed> ->
                            data
                        }
                    }
                }
        }

    private fun transform(either: Either<Failure, Data>): Either<Failure, Data> =
        either.map { data: Data ->
            data
        }
}