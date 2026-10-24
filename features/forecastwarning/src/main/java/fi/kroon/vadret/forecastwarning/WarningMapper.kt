package fi.kroon.vadret.forecastwarning

import fi.kroon.vadret.core.R
import fi.kroon.vadret.data.aggregatedfeed.model.AggregatedFeed
import fi.kroon.vadret.forecastwarning.model.ElapsedTime
import fi.kroon.vadret.forecastwarning.model.WarningItem
import fi.kroon.vadret.forecastwarning.model.WarningSource
import kotlin.time.Duration
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Instant

internal object WarningMapper {

    private const val KRISINFORMATION_SOURCE_ID = 0
    private const val SMHI_SOURCE_ID = 1
    private const val TWO = 2

    /**
     *  SMHI lists areas as "a,b,c".
     */
    private val commaWithoutSpace = Regex(pattern = ",(?=\\S)")

    operator fun invoke(entityList: List<AggregatedFeed>, now: Instant): List<WarningItem> =
        entityList.map { aggregatedFeed: AggregatedFeed ->
            WarningItem(
                identifier = aggregatedFeed.identifier,
                headline = aggregatedFeed.headline,
                bodyText = aggregatedFeed.bodyText,
                preamble = aggregatedFeed.preamble.replace(
                    regex = commaWithoutSpace,
                    replacement = ", "
                ),
                source = toWarningSource(sourceId = aggregatedFeed.sourceId),
                elapsedTime = toElapsedTime(
                    published = Instant.parse(input = aggregatedFeed.published),
                    now = now
                )
            )
        }

    fun toWarningSource(sourceId: Int): WarningSource =
        when (sourceId) {
            KRISINFORMATION_SOURCE_ID -> WarningSource.KRISINFORMATION
            SMHI_SOURCE_ID -> WarningSource.SMHI
            else -> WarningSource.UNKNOWN
        }

    /**
     *  Feed sources are stored by name, see [WarningSource.displayName].
     */
    fun toWarningSource(name: String): WarningSource =
        WarningSource.entries.firstOrNull { warningSource: WarningSource ->
            warningSource.displayName == name
        } ?: WarningSource.UNKNOWN

    fun toElapsedTime(published: Instant, now: Instant): ElapsedTime {
        val elapsed: Duration = now - published
        return when {
            elapsed.isNegative() -> ElapsedTime(resourceId = R.string.in_the_future)
            elapsed < 1.minutes -> ElapsedTime(resourceId = R.string.moments_ago)
            elapsed < TWO.minutes -> ElapsedTime(resourceId = R.string.a_minute_ago)
            elapsed < 1.hours -> ElapsedTime(
                resourceId = R.string.minutes_ago,
                count = elapsed.inWholeMinutes
            )
            elapsed < TWO.hours -> ElapsedTime(resourceId = R.string.an_hour_ago)
            elapsed < 1.days -> ElapsedTime(
                resourceId = R.string.hours_ago,
                count = elapsed.inWholeHours
            )
            elapsed < TWO.days -> ElapsedTime(resourceId = R.string.yesterday)
            else -> ElapsedTime(
                resourceId = R.string.days_ago,
                count = elapsed.inWholeDays
            )
        }
    }
}