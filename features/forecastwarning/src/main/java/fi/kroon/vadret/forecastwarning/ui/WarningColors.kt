package fi.kroon.vadret.forecastwarning.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.graphics.Color
import fi.kroon.vadret.aggregatedfeed.model.FeedSource
import fi.kroon.vadret.ui.VadretTheme
import fi.kroon.vadret.warningfilter.model.DistrictCategory

internal data class ChipColors(
    val container: Color,
    val stroke: Color
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