package fi.kroon.vadret.forecastwarning.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.graphics.Color
import fi.kroon.vadret.aggregatedfeed.model.FeedSource
import fi.kroon.vadret.aggregatedfeed.model.WarningLevel
import fi.kroon.vadret.core.R
import fi.kroon.vadret.ui.VadretTheme
import fi.kroon.vadret.warningfilter.model.DistrictCategory

internal data class ChipColors(
    val container: Color,
    val stroke: Color
)

internal data class LevelColors(
    val container: Color,
    val content: Color
)

@Composable
@ReadOnlyComposable
internal fun FeedSource.chipColors(): ChipColors =
    when (this) {
        FeedSource.KRISINFORMATION -> ChipColors(
            container = VadretTheme.colors.krisinformation,
            stroke = VadretTheme.colors.krisinformationStroke
        )

        FeedSource.SMHI -> ChipColors(
            container = VadretTheme.colors.smhi,
            stroke = VadretTheme.colors.smhiStroke
        )
    }

@Composable
@ReadOnlyComposable
internal fun WarningLevel.levelColors(): LevelColors =
    when (this) {
        WarningLevel.MESSAGE -> LevelColors(
            container = VadretTheme.colors.warningMessage,
            content = VadretTheme.colors.onWarningMessage
        )

        WarningLevel.YELLOW -> LevelColors(
            container = VadretTheme.colors.warningYellow,
            content = VadretTheme.colors.onWarningYellow
        )

        WarningLevel.ORANGE -> LevelColors(
            container = VadretTheme.colors.warningOrange,
            content = VadretTheme.colors.onWarningOrange
        )

        WarningLevel.RED -> LevelColors(
            container = VadretTheme.colors.warningRed,
            content = VadretTheme.colors.onWarningRed
        )
    }

internal fun WarningLevel.labelResourceId(): Int =
    when (this) {
        WarningLevel.MESSAGE -> R.string.warning_level_message
        WarningLevel.YELLOW -> R.string.warning_level_yellow
        WarningLevel.ORANGE -> R.string.warning_level_orange
        WarningLevel.RED -> R.string.warning_level_red
    }

internal fun WarningLevel.iconResourceId(): Int =
    when (this) {
        WarningLevel.MESSAGE -> R.drawable.ic_info_filled

        WarningLevel.YELLOW,
        WarningLevel.ORANGE,
        WarningLevel.RED -> R.drawable.ic_warning_filled
    }

@Composable
@ReadOnlyComposable
internal fun DistrictCategory.chipColors(): ChipColors =
    when (this) {
        DistrictCategory.COUNTY -> ChipColors(
            container = VadretTheme.colors.districtLand,
            stroke = VadretTheme.colors.districtLandStroke
        )

        DistrictCategory.SEA_DISTRICT -> ChipColors(
            container = VadretTheme.colors.districtSea,
            stroke = VadretTheme.colors.districtSeaStroke
        )

        DistrictCategory.OTHER -> ChipColors(
            container = VadretTheme.colors.unknownSource,
            stroke = VadretTheme.colors.unknownSource
        )
    }