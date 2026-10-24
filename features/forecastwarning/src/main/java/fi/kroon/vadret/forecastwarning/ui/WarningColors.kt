package fi.kroon.vadret.forecastwarning.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.graphics.Color
import fi.kroon.vadret.forecastwarning.model.WarningSource
import fi.kroon.vadret.ui.VadretTheme

private const val LAND_CATEGORY = "land"
private const val SEA_CATEGORY = "sea"

internal data class ChipColors(
    val container: Color,
    val stroke: Color
)

@Composable
@ReadOnlyComposable
internal fun WarningSource.chipColors(): ChipColors =
    when (this) {
        WarningSource.KRISINFORMATION -> ChipColors(
            container = VadretTheme.colors.krisinformation,
            stroke = VadretTheme.colors.krisinformationStroke
        )
        WarningSource.SMHI -> ChipColors(
            container = VadretTheme.colors.smhi,
            stroke = VadretTheme.colors.smhiStroke
        )
        WarningSource.UNKNOWN -> ChipColors(
            container = VadretTheme.colors.unknownSource,
            stroke = VadretTheme.colors.unknownSource
        )
    }

/**
 *  @param category "land" or "sea", as stored for the district.
 */
@Composable
@ReadOnlyComposable
internal fun districtChipColors(category: String): ChipColors =
    when (category) {
        LAND_CATEGORY -> ChipColors(
            container = VadretTheme.colors.districtLand,
            stroke = VadretTheme.colors.districtLandStroke
        )
        SEA_CATEGORY -> ChipColors(
            container = VadretTheme.colors.districtSea,
            stroke = VadretTheme.colors.districtSeaStroke
        )
        else -> ChipColors(
            container = VadretTheme.colors.unknownSource,
            stroke = VadretTheme.colors.unknownSource
        )
    }