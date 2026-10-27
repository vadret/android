package fi.kroon.vadret.forecastwarning.extension

import fi.kroon.vadret.aggregatedfeed.model.AggregatedFeed
import fi.kroon.vadret.aggregatedfeed.model.FeedDetail
import fi.kroon.vadret.aggregatedfeed.model.FeedDetailKind
import fi.kroon.vadret.core.resources.Res
import fi.kroon.vadret.core.resources.warning_detail_advice
import fi.kroon.vadret.core.resources.warning_detail_comment
import fi.kroon.vadret.core.resources.warning_detail_description
import fi.kroon.vadret.core.resources.warning_detail_what_happens
import fi.kroon.vadret.core.resources.warning_detail_where
import fi.kroon.vadret.forecastwarning.model.WarningDetail
import fi.kroon.vadret.forecastwarning.model.WarningItem
import fi.kroon.vadret.ui.extension.toElapsedTime
import kotlinx.collections.immutable.toImmutableList
import kotlin.time.Instant

private val commaWithoutSpace = Regex(pattern = ",(?=\\S)")

private const val COMMA_WITH_SPACE = ", "

internal fun AggregatedFeed.toWarningItem(now: Instant): WarningItem {
    val spacedAreaName: String? = areaName?.withSpacedCommas()
    return WarningItem(
        identifier = identifier,
        headline = headline,
        areaName = spacedAreaName,
        warningLevel = warningLevel,
        details = details
            .filterNot { feedDetail: FeedDetail -> feedDetail.repeatsArea(spacedAreaName = spacedAreaName) }
            .map { feedDetail: FeedDetail -> feedDetail.toWarningDetail() }
            .toImmutableList(),
        source = source,
        elapsedTime = publishedAt.toElapsedTime(now = now)
    )
}

private fun FeedDetail.repeatsArea(spacedAreaName: String?): Boolean =
    kind == FeedDetailKind.WHERE &&
        text.withSpacedCommas() == spacedAreaName

private fun FeedDetail.toWarningDetail(): WarningDetail =
    when (kind) {
        FeedDetailKind.WHAT_HAPPENS -> WarningDetail.Labelled(
            labelResource = Res.string.warning_detail_what_happens,
            text = text
        )

        FeedDetailKind.DESCRIPTION -> WarningDetail.Labelled(
            labelResource = Res.string.warning_detail_description,
            text = text
        )

        FeedDetailKind.WHERE -> WarningDetail.Labelled(
            labelResource = Res.string.warning_detail_where,
            text = text
        )

        FeedDetailKind.ADVICE -> WarningDetail.Labelled(
            labelResource = Res.string.warning_detail_advice,
            text = text
        )

        FeedDetailKind.COMMENT -> WarningDetail.Labelled(
            labelResource = Res.string.warning_detail_comment,
            text = text
        )

        FeedDetailKind.SUMMARY -> WarningDetail.Paragraph(text = text.withSpacedCommas())

        FeedDetailKind.BODY -> WarningDetail.Paragraph(text = text)

        FeedDetailKind.OTHER -> WarningDetail.Titled(
            title = title,
            text = text
        )
    }

private fun String.withSpacedCommas(): String =
    replace(
        regex = commaWithoutSpace,
        replacement = COMMA_WITH_SPACE
    )