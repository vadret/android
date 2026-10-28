package fi.kroon.vadret.forecastwarning.model

import androidx.compose.runtime.Immutable

@Immutable
internal data class WarningMapArea(
    val geoJson: String?,
    val bounds: MapBounds
)