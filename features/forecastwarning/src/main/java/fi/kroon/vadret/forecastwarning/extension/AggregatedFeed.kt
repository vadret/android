package fi.kroon.vadret.forecastwarning.extension

import fi.kroon.vadret.aggregatedfeed.model.AggregatedFeed
import fi.kroon.vadret.forecastwarning.model.WarningItem
import kotlin.time.Instant

/**
 *  SMHI lists areas as "a,b,c".
 */
private val commaWithoutSpace = Regex(pattern = ",(?=\\S)")

private const val COMMA_WITH_SPACE = ", "

internal fun AggregatedFeed.toWarningItem(now: Instant): WarningItem =
    WarningItem(
        identifier = identifier,
        headline = headline,
        bodyText = bodyText,
        preamble = preamble.replace(
            regex = commaWithoutSpace,
            replacement = COMMA_WITH_SPACE
        ),
        source = source,
        elapsedTime = publishedAt.toElapsedTime(now = now)
    )