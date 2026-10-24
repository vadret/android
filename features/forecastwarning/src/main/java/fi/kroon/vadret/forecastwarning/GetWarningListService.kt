package fi.kroon.vadret.forecastwarning

import fi.kroon.vadret.data.aggregatedfeed.exception.AggregatedFeedFailure
import fi.kroon.vadret.data.aggregatedfeed.model.AggregatedFeed
import fi.kroon.vadret.data.district.exception.DistrictFailure
import fi.kroon.vadret.data.failure.Failure
import fi.kroon.vadret.forecastwarning.aggregatedfeed.GetAggregatedFeedLastCheckedTask
import fi.kroon.vadret.forecastwarning.aggregatedfeed.GetAggregatedFeedService
import fi.kroon.vadret.forecastwarning.aggregatedfeed.SetAggregatedFeedLastCheckedTask
import fi.kroon.vadret.forecastwarning.district.GetDistrictNetService
import fi.kroon.vadret.forecastwarning.districtpreference.GetEnabledDistrictPreferenceIdsTask
import fi.kroon.vadret.forecastwarning.feedsource.GetFeedSourceNetService
import fi.kroon.vadret.forecastwarning.feedsourcepreference.GetEnabledFeedSourcePreferenceIdsTask
import fi.kroon.vadret.util.extension.asLeft
import fi.kroon.vadret.util.extension.asRight
import io.github.sphrak.either.Either
import io.github.sphrak.either.extension.onResult
import io.github.sphrak.either.flatMap
import timber.log.Timber
import javax.inject.Inject

/**
 *  Makes sure districts, feed sources and their default
 *  preferences exist, then loads the feed for the enabled ones.
 */
class GetWarningListService @Inject constructor(
    private val getDistrictNetService: GetDistrictNetService,
    private val getFeedSourceNetService: GetFeedSourceNetService,
    private val countDistrictPreferenceEntityTask: CountDistrictPreferenceEntityTask,
    private val countFeedSourcePreferenceEntityTask: CountFeedSourcePreferenceEntityTask,
    private val setDefaultDistrictPreferenceEntityService: SetDefaultDistrictPreferenceEntityService,
    private val setDefaultFeedSourcePreferenceEntityService: SetDefaultFeedSourcePreferenceEntityService,
    private val getAggregatedFeedLastCheckedTask: GetAggregatedFeedLastCheckedTask,
    private val getEnabledDistrictPreferenceIdsTask: GetEnabledDistrictPreferenceIdsTask,
    private val getEnabledFeedSourcePreferenceIdsTask: GetEnabledFeedSourcePreferenceIdsTask,
    private val getAggregatedFeedService: GetAggregatedFeedService,
    private val setAggregatedFeedLastCheckedTask: SetAggregatedFeedLastCheckedTask
) {

    suspend operator fun invoke(forceNet: Boolean): Either<Failure, List<AggregatedFeed>> =
        requireAvailable(either = getDistrictNetService())
            .flatMap { requireAvailable(either = getFeedSourceNetService()) }
            .flatMap { ensureDistrictPreferences() }
            .flatMap { ensureFeedSourcePreferences() }
            .flatMap { getAggregatedFeedLastCheckedTask() }
            .flatMap { timeStamp: Long -> getAggregatedFeed(timeStamp = timeStamp, forceNet = forceNet) }

    private suspend fun getAggregatedFeed(timeStamp: Long, forceNet: Boolean): Either<Failure, List<AggregatedFeed>> =
        getEnabledDistrictPreferenceIdsTask().flatMap { districtIds: List<Int> ->
            getEnabledFeedSourcePreferenceIdsTask().flatMap { feedSourceIds: List<Int> ->
                getAggregatedFeedService(
                    timeStamp = timeStamp,
                    forceNet = forceNet,
                    districtIds = districtIds,
                    feedSourceIds = feedSourceIds
                ).flatMap { data: GetAggregatedFeedService.Data ->
                    setLastChecked(timeStamp = data.timeStamp)
                    data.aggregatedFeedList.asRight()
                }
            }
        }

    /**
     *  A lost timestamp only means the next load skips the cache.
     */
    private suspend fun setLastChecked(timeStamp: Long) {
        setAggregatedFeedLastCheckedTask(value = timeStamp)
            .onResult(
                { failure: Failure ->
                    Timber.e("Failed storing last checked: $failure")
                },
                {}
            )
    }

    private suspend fun ensureDistrictPreferences(): Either<Failure, Unit> =
        countDistrictPreferenceEntityTask().flatMap { isAvailable: Boolean ->
            if (isAvailable) {
                Unit.asRight()
            } else {
                requireStored(either = setDefaultDistrictPreferenceEntityService())
            }
        }

    private suspend fun ensureFeedSourcePreferences(): Either<Failure, Unit> =
        countFeedSourcePreferenceEntityTask().flatMap { isAvailable: Boolean ->
            if (isAvailable) {
                Unit.asRight()
            } else {
                requireStored(either = setDefaultFeedSourcePreferenceEntityService())
            }
        }

    private fun requireAvailable(either: Either<Failure, Boolean>): Either<Failure, Unit> =
        either.flatMap { isAvailable: Boolean ->
            if (isAvailable) {
                Unit.asRight()
            } else {
                DistrictFailure.DistrictNotAvailable.asLeft()
            }
        }

    private fun requireStored(either: Either<Failure, Boolean>): Either<Failure, Unit> =
        either.flatMap { isStored: Boolean ->
            if (isStored) {
                Unit.asRight()
            } else {
                AggregatedFeedFailure.LoadingPreferenceFailed.asLeft()
            }
        }
}