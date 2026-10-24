package fi.kroon.vadret.forecastwarning

import fi.kroon.vadret.aggregatedfeed.AggregatedFeedRepository
import fi.kroon.vadret.aggregatedfeed.model.AggregatedFeed
import fi.kroon.vadret.aggregatedfeed.model.FeedSource
import fi.kroon.vadret.common.Failure
import fi.kroon.vadret.warningfilter.WarningFilterRepository
import fi.kroon.vadret.warningfilter.model.DistrictOption
import fi.kroon.vadret.warningfilter.model.FeedSourceOption
import fi.kroon.vadret.warningfilter.model.WarningFilter
import io.github.sphrak.either.Either
import io.github.sphrak.either.flatMap

internal class DefaultGetWarningsTask(
    private val warningFilterRepository: WarningFilterRepository,
    private val aggregatedFeedRepository: AggregatedFeedRepository
) : GetWarningsTask {

    override suspend fun invoke(isRefresh: Boolean): Either<Failure, List<AggregatedFeed>> =
        warningFilterRepository
            .getWarningFilter()
            .flatMap { warningFilter: WarningFilter ->
                aggregatedFeedRepository.getAggregatedFeed(
                    feedSources = warningFilter.enabledFeedSources(),
                    districtIds = warningFilter.enabledDistrictIds(),
                    isRefresh = isRefresh
                )
            }

    private fun WarningFilter.enabledFeedSources(): Set<FeedSource> =
        feedSources
            .filter { feedSourceOption: FeedSourceOption -> feedSourceOption.isEnabled }
            .map { feedSourceOption: FeedSourceOption -> feedSourceOption.feedSource }
            .toSet()

    private fun WarningFilter.enabledDistrictIds(): Set<Int> =
        districts
            .filter { districtOption: DistrictOption -> districtOption.isEnabled }
            .map { districtOption: DistrictOption -> districtOption.districtId }
            .toSet()
}