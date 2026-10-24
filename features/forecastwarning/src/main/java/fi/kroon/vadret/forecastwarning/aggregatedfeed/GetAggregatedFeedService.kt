package fi.kroon.vadret.forecastwarning.aggregatedfeed

import fi.kroon.vadret.data.aggregatedfeed.model.AggregatedFeed
import fi.kroon.vadret.data.failure.Failure
import fi.kroon.vadret.shared.IService
import fi.kroon.vadret.util.FIVE_MINUTES_IN_MILLIS
import io.github.sphrak.either.Either
import io.github.sphrak.either.extension.onResult
import io.github.sphrak.either.flatMap
import io.github.sphrak.either.map
import timber.log.Timber
import javax.inject.Inject

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

    suspend operator fun invoke(timeStamp: Long, forceNet: Boolean, districtIds: List<Int>, feedSourceIds: List<Int>): Either<Failure, Data> =
        getAlert(
            Data(
                timeStamp = timeStamp,
                forceNet = forceNet,
                counties = districtIds,
                feeds = feedSourceIds
            )
        ).map { dataIn: Data ->
            dataIn.copy(
                timeStamp = currentTimeMillis
            )
        }

    private suspend fun getAlert(data: Data): Either<Failure, Data> =
        with(data) {
            when {
                forceNet || (currentTimeMillis > (timeStamp + FIVE_MINUTES_IN_MILLIS)) -> getAggregatedFeedNet(data)
                else -> getAggregatedFeedCache(data) ?: run {
                    Timber.d("Cache was empty, fetching from network.")
                    getAggregatedFeedNet(data)
                }
            }
        }

    private suspend fun getAggregatedFeedNet(data: Data): Either<Failure, Data> =
        getAggregatedFeedTask(counties = data.counties, feeds = data.feeds)
            .map { aggregatedFeedList: List<AggregatedFeed> ->
                data.copy(aggregatedFeedList = aggregatedFeedList)
            }.let { updateCache(it) }

    /**
     *  Memory cache first, then disk. Null when neither
     *  holds a non-empty feed.
     */
    private suspend fun getAggregatedFeedCache(data: Data): Either<Failure, Data>? =
        (getAggregatedFeedMemoryCacheTask().takeIf(::hasFeed) ?: getAggregatedFeedDiskCacheTask().takeIf(::hasFeed))
            ?.map { aggregatedFeedList: List<AggregatedFeed> ->
                Timber.d("Fetched from cache $aggregatedFeedList")
                data.copy(aggregatedFeedList = aggregatedFeedList)
            }

    private fun hasFeed(result: Either<Failure, List<AggregatedFeed>>): Boolean =
        result.onResult(
            {
                false
            },
            { aggregatedFeedList: List<AggregatedFeed> ->
                aggregatedFeedList.isNotEmpty()
            }
        )

    private suspend fun updateCache(either: Either<Failure, Data>): Either<Failure, Data> =
        either.flatMap { data: Data ->
            Timber.i("Updating cache")
            val memoryEither: Either<Failure, List<AggregatedFeed>> = setAggregatedFeedMemoryCacheTask(data.aggregatedFeedList)
            val diskEither: Either<Failure, List<AggregatedFeed>> = setAggregatedFeedDiskCacheTask(data.aggregatedFeedList)
            memoryEither.flatMap { _: List<AggregatedFeed> ->
                diskEither.map { _: List<AggregatedFeed> ->
                    data
                }
            }
        }
}