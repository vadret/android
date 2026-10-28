package fi.kroon.vadret.forecastwarning.model

import androidx.compose.runtime.Immutable
import fi.kroon.vadret.aggregatedfeed.model.FeedSource
import fi.kroon.vadret.aggregatedfeed.model.WarningLevel
import fi.kroon.vadret.ui.model.ElapsedTime
import kotlinx.collections.immutable.ImmutableList

@Immutable
internal data class WarningItem(
    val identifier: String,
    val headline: String,
    val areaName: String?,
    val warningLevel: WarningLevel?,
    val source: FeedSource,
    val isPublicAlert: Boolean,
    val publisher: String,
    val byline: String,
    val elapsedTime: ElapsedTime,
    val startsAtText: String?,
    val endsAtText: String?,
    val details: ImmutableList<WarningDetail>,
    val mapArea: WarningMapArea
)