package fi.kroon.vadret.forecastwarning.model

import fi.kroon.vadret.aggregatedfeed.model.FeedSource
import fi.kroon.vadret.aggregatedfeed.model.WarningLevel
import fi.kroon.vadret.ui.model.ElapsedTime
import kotlinx.collections.immutable.ImmutableList

data class WarningItem(
    val identifier: String,
    val headline: String,
    val areaName: String?,
    val warningLevel: WarningLevel?,
    val details: ImmutableList<WarningDetail>,
    val source: FeedSource,
    val elapsedTime: ElapsedTime
)