package fi.kroon.vadret.radar

import android.graphics.Bitmap
import androidx.annotation.StringRes
import fi.kroon.vadret.location.model.Location
import fi.kroon.vadret.radar.model.RadarFrame
import fi.kroon.vadret.radar.model.RadarLayer
import fi.kroon.vadret.radar.model.WindTimeFrame
import fi.kroon.vadret.radar.wind.WindField
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

data class RadarState(
    val layer: RadarLayer = RadarLayer.PRECIPITATION,
    val precipitationFrames: ImmutableList<RadarFrame> = persistentListOf(),
    val precipitationIndex: Int = 0,
    val precipitationOverlay: Bitmap? = null,
    val windFrames: ImmutableList<WindTimeFrame> = persistentListOf(),
    val windIndex: Int = 0,
    val windField: WindField? = null,
    val deviceLocation: Location? = null,
    val isPlaying: Boolean = false,
    val isLoading: Boolean = true,
    @StringRes
    val errorMessageResourceId: Int? = null
)