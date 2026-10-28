package fi.kroon.vadret.warning

import fi.kroon.vadret.aggregatedfeed.FeedRepository
import fi.kroon.vadret.aggregatedfeed.model.AggregatedFeed
import fi.kroon.vadret.aggregatedfeed.model.FeedArea
import fi.kroon.vadret.aggregatedfeed.model.FeedRegion
import fi.kroon.vadret.aggregatedfeed.model.FeedSource
import fi.kroon.vadret.aggregatedfeed.model.GeoLine
import fi.kroon.vadret.aggregatedfeed.model.GeoPolygon
import fi.kroon.vadret.aggregatedfeed.model.GeoPosition
import fi.kroon.vadret.aggregatedfeed.model.SeenWarning
import fi.kroon.vadret.aggregatedfeed.model.WarningLevel
import fi.kroon.vadret.common.Failure
import io.github.sphrak.either.Either
import io.github.sphrak.either.extension.asRight
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlin.time.Instant

internal fun square(
    south: Double,
    west: Double,
    north: Double,
    east: Double
): List<GeoPosition> =
    listOf(
        GeoPosition(
            latitude = south,
            longitude = west
        ),
        GeoPosition(
            latitude = south,
            longitude = east
        ),
        GeoPosition(
            latitude = north,
            longitude = east
        ),
        GeoPosition(
            latitude = north,
            longitude = west
        ),
        GeoPosition(
            latitude = south,
            longitude = west
        )
    )

internal fun aggregatedFeed(
    identifier: String,
    publishedAt: Instant = Instant.parse(input = "2026-10-24T08:00:00Z"),
    source: FeedSource = FeedSource.SMHI,
    polygons: List<GeoPolygon> = emptyList(),
    lines: List<GeoLine> = emptyList(),
    regions: List<FeedRegion> = emptyList()
): AggregatedFeed =
    AggregatedFeed(
        identifier = identifier,
        headline = "Vind",
        areaName = "Jämtlands län",
        warningLevel = WarningLevel.YELLOW,
        details = emptyList(),
        source = source,
        isPublicAlert = false,
        senderName = null,
        publishedAt = publishedAt,
        startsAt = null,
        endsAt = null,
        area = FeedArea(
            polygons = polygons,
            lines = lines,
            regions = regions
        )
    )

internal class FakeFeedRepository(
    var result: Either<Failure, List<AggregatedFeed>> = listOf(aggregatedFeed(identifier = "smhi-1-1")).asRight()
) : FeedRepository {

    val refreshes = mutableListOf<Boolean>()
    val seenWarnings: MutableStateFlow<Either<Failure, List<SeenWarning>>> = MutableStateFlow(value = emptyList<SeenWarning>().asRight())
    val markedSeen = mutableListOf<String>()

    override suspend fun getAggregatedFeed(isRefresh: Boolean): Either<Failure, List<AggregatedFeed>> {
        refreshes.add(element = isRefresh)
        return result
    }

    override fun observeSeenWarnings(): Flow<Either<Failure, List<SeenWarning>>> = seenWarnings

    override suspend fun markSeen(warningIdentifier: String): Either<Failure, Unit> {
        markedSeen.add(element = warningIdentifier)
        return Unit.asRight()
    }
}