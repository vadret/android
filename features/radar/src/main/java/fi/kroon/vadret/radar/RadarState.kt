package fi.kroon.vadret.radar

import androidx.annotation.StringRes
import fi.kroon.vadret.location.model.Location
import fi.kroon.vadret.radar.model.RadarFrame
import fi.kroon.vadret.radar.model.RadarLayer
import fi.kroon.vadret.radar.model.WindTimeFrame
import fi.kroon.vadret.radar.precipitation.PrecipitationOverlay
import fi.kroon.vadret.radar.wind.WindField
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

internal data class RadarState(
    val layer: RadarLayer = RadarLayer.PRECIPITATION,
    val precipitationFrames: ImmutableList<RadarFrame> = persistentListOf(),
    val precipitationIndex: Int = 0,
    val precipitationOverlay: PrecipitationOverlay? = null,
    val windFrames: ImmutableList<WindTimeFrame> = persistentListOf(),
    val windIndex: Int = 0,
    val windField: WindField? = null,
    val windTransitionField: WindField? = null,
    val deviceLocation: Location? = null,
    val isPlaying: Boolean = false,
    val isLoading: Boolean = true,
    val isMapLoaded: Boolean = false,
    val isPrecipitationUnavailable: Boolean = false,
    val areRadarStationsShown: Boolean = false,
    @StringRes
    val errorMessageResourceId: Int? = null
) {

    val isInitialLoading: Boolean = !isMapLoaded || (precipitationOverlay == null && !isPrecipitationUnavailable)

    val shownWindField: WindField? = windTransitionField ?: windField
}