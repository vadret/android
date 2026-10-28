package fi.kroon.vadret.forecastwarning

import fi.kroon.vadret.aggregatedfeed.model.AggregatedFeed
import fi.kroon.vadret.aggregatedfeed.model.FeedArea
import fi.kroon.vadret.aggregatedfeed.model.FeedDetail
import fi.kroon.vadret.aggregatedfeed.model.FeedRegion
import fi.kroon.vadret.aggregatedfeed.model.FeedSource
import fi.kroon.vadret.aggregatedfeed.model.GeoLine
import fi.kroon.vadret.aggregatedfeed.model.GeoPolygon
import fi.kroon.vadret.aggregatedfeed.model.WarningLevel
import fi.kroon.vadret.common.Failure
import fi.kroon.vadret.warning.GetWarningsTask
import io.github.sphrak.either.Either
import io.github.sphrak.either.extension.asRight
import kotlin.time.Instant

internal fun aggregatedFeed(
    identifier: String,
    publishedAt: Instant = Instant.parse(input = "2026-10-24T08:00:00Z"),
    source: FeedSource = FeedSource.SMHI,
    headline: String = "Vind",
    areaName: String? = "Gotlands län",
    warningLevel: WarningLevel? = WarningLevel.YELLOW,
    details: List<FeedDetail> = emptyList(),
    senderName: String? = null,
    startsAt: Instant? = null,
    endsAt: Instant? = null,
    polygons: List<GeoPolygon> = emptyList(),
    lines: List<GeoLine> = emptyList(),
    regions: List<FeedRegion> = emptyList()
): AggregatedFeed =
    AggregatedFeed(
        identifier = identifier,
        headline = headline,
        areaName = areaName,
        warningLevel = warningLevel,
        details = details,
        source = source,
        isPublicAlert = false,
        senderName = senderName,
        publishedAt = publishedAt,
        startsAt = startsAt,
        endsAt = endsAt,
        area = FeedArea(
            polygons = polygons,
            lines = lines,
            regions = regions
        )
    )

internal class FakeGetWarningsTask(
    var result: Either<Failure, List<AggregatedFeed>> = listOf(
        aggregatedFeed(
            identifier = "smhi-1-2",
            publishedAt = Instant.parse(input = "2026-10-24T08:00:00Z")
        ),
        aggregatedFeed(
            identifier = "smhi-1-1",
            publishedAt = Instant.parse(input = "2026-10-24T07:00:00Z")
        )
    ).asRight()
) : GetWarningsTask {

    val refreshes = mutableListOf<Boolean>()

    override suspend fun invoke(isRefresh: Boolean): Either<Failure, List<AggregatedFeed>> {
        refreshes.add(element = isRefresh)
        return result
    }
}

internal class FakeMarkWarningSeenTask : MarkWarningSeenTask {

    val warningIdentifiers = mutableListOf<String>()

    override suspend fun invoke(warningIdentifier: String): Either<Failure, Unit> {
        warningIdentifiers.add(element = warningIdentifier)
        return Unit.asRight()
    }
}