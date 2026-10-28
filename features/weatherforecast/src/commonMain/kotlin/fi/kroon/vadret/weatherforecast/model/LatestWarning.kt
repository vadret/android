package fi.kroon.vadret.weatherforecast.model

import androidx.compose.runtime.Immutable
import fi.kroon.vadret.aggregatedfeed.model.FeedSource
import fi.kroon.vadret.aggregatedfeed.model.WarningLevel
import kotlinx.collections.immutable.ImmutableList

@Immutable
data class LatestWarning(
    val warningLevel: WarningLevel?,
    val source: FeedSource,
    val isPublicAlert: Boolean,
    val headline: String,
    val endsAtText: String?,
    val warningIdentifiers: ImmutableList<String>
)