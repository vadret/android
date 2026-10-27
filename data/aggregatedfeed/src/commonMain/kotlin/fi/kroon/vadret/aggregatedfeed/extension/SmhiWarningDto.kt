package fi.kroon.vadret.aggregatedfeed.extension

import fi.kroon.vadret.aggregatedfeed.local.AggregatedFeedEntity
import fi.kroon.vadret.aggregatedfeed.local.FeedDetailEntity
import fi.kroon.vadret.aggregatedfeed.model.FeedDetailKind
import fi.kroon.vadret.aggregatedfeed.model.FeedSource
import fi.kroon.vadret.aggregatedfeed.model.WarningLevel
import fi.kroon.vadret.aggregatedfeed.remote.SmhiAffectedAreaDto
import fi.kroon.vadret.aggregatedfeed.remote.SmhiDescriptionDto
import fi.kroon.vadret.aggregatedfeed.remote.SmhiWarningAreaDto
import fi.kroon.vadret.aggregatedfeed.remote.SmhiWarningDto
import fi.kroon.vadret.logging.Logger
import fi.kroon.vadret.logging.LoggerFactory
import kotlin.time.Instant

private val logger: Logger = LoggerFactory.getLogger(tag = "fi.kroon.vadret.aggregatedfeed")

private val detailKindsByCode: Map<String, FeedDetailKind> = mapOf(
    "HAPPENS" to FeedDetailKind.WHAT_HAPPENS,
    "INCIDENT" to FeedDetailKind.DESCRIPTION,
    "WHERE" to FeedDetailKind.WHERE,
    "AFFECT" to FeedDetailKind.ADVICE,
    "COMMENTS" to FeedDetailKind.COMMENT
)

internal fun SmhiWarningDto.toAggregatedFeedEntities(districtIds: Set<Int>): List<AggregatedFeedEntity> =
    warningAreas
        .orEmpty()
        .filter { smhiWarningAreaDto: SmhiWarningAreaDto -> smhiWarningAreaDto.affects(districtIds = districtIds) }
        .mapNotNull { smhiWarningAreaDto: SmhiWarningAreaDto -> toAggregatedFeedEntity(smhiWarningAreaDto = smhiWarningAreaDto) }

private fun SmhiWarningAreaDto.affects(districtIds: Set<Int>): Boolean =
    affectedAreas
        .orEmpty()
        .any { smhiAffectedAreaDto: SmhiAffectedAreaDto -> smhiAffectedAreaDto.id in districtIds }

private fun SmhiWarningDto.toAggregatedFeedEntity(smhiWarningAreaDto: SmhiWarningAreaDto): AggregatedFeedEntity? {
    val publishedAt: Instant? = Instant.parseOrNull(input = smhiWarningAreaDto.published)
    if (publishedAt == null) {
        logger.warn { "Skipping SMHI warning area ${smhiWarningAreaDto.id}, published ${smhiWarningAreaDto.published}" }
        return null
    }
    return AggregatedFeedEntity(
        identifier = "smhi-$id-${smhiWarningAreaDto.id}",
        headline = smhiWarningAreaDto.eventDescription.sv,
        areaName = smhiWarningAreaDto.areaName.sv,
        warningLevel = smhiWarningAreaDto.toWarningLevel()?.name,
        details = (smhiWarningAreaDto.descriptions.orEmpty() + descriptions.orEmpty())
            .map { smhiDescriptionDto: SmhiDescriptionDto -> smhiDescriptionDto.toFeedDetailEntity() },
        source = FeedSource.SMHI.name,
        publishedAtEpochMillis = publishedAt.toEpochMilliseconds()
    )
}

private fun SmhiWarningAreaDto.toWarningLevel(): WarningLevel? {
    val warningLevel: WarningLevel? = WarningLevel.entries.firstOrNull { level: WarningLevel -> level.name == this.warningLevel.code }
    if (warningLevel == null) {
        logger.warn { "Unknown SMHI warning level ${this.warningLevel.code} on warning area $id" }
    }
    return warningLevel
}

private fun SmhiDescriptionDto.toFeedDetailEntity(): FeedDetailEntity =
    FeedDetailEntity(
        kind = (detailKindsByCode[title.code] ?: FeedDetailKind.OTHER).name,
        title = title.sv,
        text = text.sv
    )