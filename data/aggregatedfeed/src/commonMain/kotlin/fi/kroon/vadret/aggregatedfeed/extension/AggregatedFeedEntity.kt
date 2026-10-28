package fi.kroon.vadret.aggregatedfeed.extension

import fi.kroon.vadret.aggregatedfeed.local.AggregatedFeedEntity
import fi.kroon.vadret.aggregatedfeed.local.FeedDetailEntity
import fi.kroon.vadret.aggregatedfeed.local.FeedRegionEntity
import fi.kroon.vadret.aggregatedfeed.local.GeoLineEntity
import fi.kroon.vadret.aggregatedfeed.local.GeoPolygonEntity
import fi.kroon.vadret.aggregatedfeed.local.GeoPositionEntity
import fi.kroon.vadret.aggregatedfeed.model.AggregatedFeed
import fi.kroon.vadret.aggregatedfeed.model.FeedArea
import fi.kroon.vadret.aggregatedfeed.model.FeedDetail
import fi.kroon.vadret.aggregatedfeed.model.FeedDetailKind
import fi.kroon.vadret.aggregatedfeed.model.FeedRegion
import fi.kroon.vadret.aggregatedfeed.model.FeedRegionKind
import fi.kroon.vadret.aggregatedfeed.model.FeedSource
import fi.kroon.vadret.aggregatedfeed.model.GeoLine
import fi.kroon.vadret.aggregatedfeed.model.GeoPolygon
import fi.kroon.vadret.aggregatedfeed.model.GeoPosition
import fi.kroon.vadret.aggregatedfeed.model.WarningLevel
import fi.kroon.vadret.logging.Logger
import fi.kroon.vadret.logging.LoggerFactory
import kotlin.time.Instant

private val logger: Logger = LoggerFactory.getLogger(tag = "fi.kroon.vadret.aggregatedfeed")

internal fun AggregatedFeedEntity.toAggregatedFeed(): AggregatedFeed? {
    val feedSource: FeedSource? = FeedSource.entries.firstOrNull { feedSource: FeedSource -> feedSource.name == source }
    if (feedSource == null) {
        logger.warn { "Skipping cached item $identifier from unknown source $source" }
        return null
    }
    return AggregatedFeed(
        identifier = identifier,
        headline = headline,
        areaName = areaName,
        warningLevel = WarningLevel.entries.firstOrNull { level: WarningLevel -> level.name == warningLevel },
        details = details.map { feedDetailEntity: FeedDetailEntity -> feedDetailEntity.toFeedDetail() },
        source = feedSource,
        isPublicAlert = isPublicAlert,
        senderName = senderName,
        publishedAt = Instant.fromEpochMilliseconds(epochMilliseconds = publishedAtEpochMillis),
        startsAt = startsAtEpochMillis.toInstantOrNull(),
        endsAt = endsAtEpochMillis.toInstantOrNull(),
        area = FeedArea(
            polygons = polygons.map { geoPolygonEntity: GeoPolygonEntity -> geoPolygonEntity.toGeoPolygon() },
            lines = lines.map { geoLineEntity: GeoLineEntity -> GeoLine(positions = geoLineEntity.positions.toGeoPositions()) },
            regions = regions.map { feedRegionEntity: FeedRegionEntity -> feedRegionEntity.toFeedRegion() }
        )
    )
}

private fun FeedDetailEntity.toFeedDetail(): FeedDetail =
    FeedDetail(
        kind = FeedDetailKind.entries.firstOrNull { feedDetailKind: FeedDetailKind -> feedDetailKind.name == kind } ?: FeedDetailKind.OTHER,
        title = title,
        text = text
    )

private fun GeoPolygonEntity.toGeoPolygon(): GeoPolygon =
    GeoPolygon(
        outline = outline.toGeoPositions(),
        holes = holes.map { hole: List<GeoPositionEntity> -> hole.toGeoPositions() }
    )

private fun List<GeoPositionEntity>.toGeoPositions(): List<GeoPosition> =
    map { geoPositionEntity: GeoPositionEntity ->
        GeoPosition(
            latitude = geoPositionEntity.latitude,
            longitude = geoPositionEntity.longitude
        )
    }

private fun FeedRegionEntity.toFeedRegion(): FeedRegion =
    FeedRegion(
        kind = FeedRegionKind.entries.firstOrNull { feedRegionKind: FeedRegionKind -> feedRegionKind.name == kind } ?: FeedRegionKind.OTHER,
        name = name
    )

private fun Long?.toInstantOrNull(): Instant? =
    if (this == null) {
        null
    } else {
        Instant.fromEpochMilliseconds(epochMilliseconds = this)
    }