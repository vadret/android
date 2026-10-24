package fi.kroon.vadret.aggregatedfeed.extension

import fi.kroon.vadret.aggregatedfeed.local.AggregatedFeedEntity
import fi.kroon.vadret.aggregatedfeed.model.FeedSource
import fi.kroon.vadret.aggregatedfeed.remote.SmhiAffectedAreaDto
import fi.kroon.vadret.aggregatedfeed.remote.SmhiDescriptionDto
import fi.kroon.vadret.aggregatedfeed.remote.SmhiWarningAreaDto
import fi.kroon.vadret.aggregatedfeed.remote.SmhiWarningDto
import fi.kroon.vadret.logging.Logger
import fi.kroon.vadret.logging.LoggerFactory
import kotlin.time.Instant

private val logger: Logger = LoggerFactory.getLogger(tag = "fi.kroon.vadret.aggregatedfeed")

private const val MESSAGE_LEVEL = "MESSAGE"
private const val PREAMBLE_SEPARATOR = " - "
private const val PARAGRAPH_SEPARATOR = "\n\n"

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
        headline = headline(smhiWarningAreaDto = smhiWarningAreaDto),
        preamble = preamble(smhiWarningAreaDto = smhiWarningAreaDto),
        bodyText = (smhiWarningAreaDto.descriptions.orEmpty() + descriptions.orEmpty())
            .joinToString(separator = PARAGRAPH_SEPARATOR) { smhiDescriptionDto: SmhiDescriptionDto ->
                "${smhiDescriptionDto.title.sv}\n${smhiDescriptionDto.text.sv}"
            },
        source = FeedSource.SMHI.name,
        publishedAtEpochMillis = publishedAt.toEpochMilliseconds()
    )
}

private fun SmhiWarningDto.headline(smhiWarningAreaDto: SmhiWarningAreaDto): String =
    if (smhiWarningAreaDto.warningLevel.code == MESSAGE_LEVEL) {
        "${smhiWarningAreaDto.warningLevel.sv}: ${event.sv}"
    } else {
        "${smhiWarningAreaDto.warningLevel.sv} varning: ${event.sv}"
    }

private fun SmhiWarningDto.preamble(smhiWarningAreaDto: SmhiWarningAreaDto): String {
    val eventDescription: String = smhiWarningAreaDto.eventDescription.sv
    return if (eventDescription == event.sv) {
        smhiWarningAreaDto.areaName.sv
    } else {
        smhiWarningAreaDto.areaName.sv + PREAMBLE_SEPARATOR + eventDescription
    }
}