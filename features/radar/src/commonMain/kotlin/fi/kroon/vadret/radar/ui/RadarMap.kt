package fi.kroon.vadret.radar.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import fi.kroon.vadret.location.model.Location
import fi.kroon.vadret.radar.model.RadarLayer
import fi.kroon.vadret.radar.precipitation.PrecipitationOverlay
import fi.kroon.vadret.radar.wind.WindAtLocation
import fi.kroon.vadret.radar.wind.WindField

@Composable
internal expect fun RadarMap(
    layer: RadarLayer,
    precipitationOverlay: PrecipitationOverlay?,
    windField: WindField?,
    deviceLocation: Location?,
    windAtLocation: WindAtLocation?,
    windSpeedText: String?,
    areRadarStationsShown: Boolean,
    obscuredBottom: Int,
    onMapLoaded: () -> Unit,
    modifier: Modifier = Modifier
)