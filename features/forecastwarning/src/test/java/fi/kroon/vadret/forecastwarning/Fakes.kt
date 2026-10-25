package fi.kroon.vadret.forecastwarning

import fi.kroon.vadret.aggregatedfeed.AggregatedFeedRepository
import fi.kroon.vadret.aggregatedfeed.model.AggregatedFeed
import fi.kroon.vadret.aggregatedfeed.model.FeedDetail
import fi.kroon.vadret.aggregatedfeed.model.FeedDetailKind
import fi.kroon.vadret.aggregatedfeed.model.FeedSource
import fi.kroon.vadret.aggregatedfeed.model.WarningLevel
import fi.kroon.vadret.common.Failure
import fi.kroon.vadret.warningfilter.WarningFilterRepository
import fi.kroon.vadret.warningfilter.model.DistrictCategory
import fi.kroon.vadret.warningfilter.model.DistrictOption
import fi.kroon.vadret.warningfilter.model.FeedSourceOption
import fi.kroon.vadret.warningfilter.model.WarningFilter
import io.github.sphrak.either.Either
import io.github.sphrak.either.extension.asRight
import kotlin.time.Instant

internal val stockholm = DistrictOption(
    districtId = 1,
    name = "Stockholms län",
    category = DistrictCategory.COUNTY,
    isEnabled = true
)

internal val gotland = DistrictOption(
    districtId = 9,
    name = "Gotlands län",
    category = DistrictCategory.COUNTY,
    isEnabled = false
)

internal val defaultWarningFilter = WarningFilter(
    districts = listOf(gotland, stockholm),
    feedSources = listOf(
        FeedSourceOption(feedSource = FeedSource.KRISINFORMATION, isEnabled = false),
        FeedSourceOption(feedSource = FeedSource.SMHI, isEnabled = true)
    )
)

internal fun aggregatedFeed(identifier: String): AggregatedFeed =
    AggregatedFeed(
        identifier = identifier,
        headline = "Vind",
        areaName = "Gotland",
        warningLevel = WarningLevel.YELLOW,
        details = listOf(
            FeedDetail(
                kind = FeedDetailKind.DESCRIPTION,
                title = "Händelsebeskrivning",
                text = "Hårda vindbyar"
            )
        ),
        source = FeedSource.SMHI,
        publishedAt = Instant.parse(input = "2026-10-24T08:00:00Z")
    )

internal class FakeWarningFilterRepository(
    var warningFilter: Either<Failure, WarningFilter> = defaultWarningFilter.asRight(),
    var setResult: Either<Failure, Unit> = Unit.asRight()
) : WarningFilterRepository {

    val storedFilters = mutableListOf<Pair<Set<Int>, Set<FeedSource>>>()

    override suspend fun getWarningFilter(): Either<Failure, WarningFilter> = warningFilter

    override suspend fun setWarningFilter(
        enabledDistrictIds: Set<Int>,
        enabledFeedSources: Set<FeedSource>
    ): Either<Failure, Unit> {
        storedFilters.add(enabledDistrictIds to enabledFeedSources)
        return setResult
    }
}

internal class FakeAggregatedFeedRepository(
    var result: Either<Failure, List<AggregatedFeed>> = listOf(aggregatedFeed(identifier = "smhi-1-1")).asRight()
) : AggregatedFeedRepository {

    val requests = mutableListOf<Triple<Set<FeedSource>, Set<Int>, Boolean>>()

    override suspend fun getAggregatedFeed(
        feedSources: Set<FeedSource>,
        districtIds: Set<Int>,
        isRefresh: Boolean
    ): Either<Failure, List<AggregatedFeed>> {
        requests.add(Triple(feedSources, districtIds, isRefresh))
        return result
    }
}