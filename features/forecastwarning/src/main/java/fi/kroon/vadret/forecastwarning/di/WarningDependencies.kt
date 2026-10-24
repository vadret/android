package fi.kroon.vadret.forecastwarning.di

import fi.kroon.vadret.aggregatedfeed.AggregatedFeedRepository
import fi.kroon.vadret.warningfilter.WarningFilterRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlin.time.Clock

/**
 *  What the warning screen needs from the app.
 */
interface WarningDependencies {
    fun aggregatedFeedRepository(): AggregatedFeedRepository
    fun warningFilterRepository(): WarningFilterRepository
    fun clock(): Clock
    fun coroutineDispatcher(): CoroutineDispatcher
}