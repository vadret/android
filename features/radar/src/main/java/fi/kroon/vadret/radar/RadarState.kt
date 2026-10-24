package fi.kroon.vadret.radar

import android.graphics.Bitmap
import androidx.annotation.StringRes
import fi.kroon.vadret.radar.model.RadarFrame

/**
 *  @param index the frame shown, the newest once loaded.
 *  @param overlay the image of [index], drawn over the map.
 */
data class RadarState(
    val frames: List<RadarFrame> = emptyList(),
    val index: Int = 0,
    val overlay: Bitmap? = null,
    val isPlaying: Boolean = false,
    val isLoading: Boolean = true,
    @StringRes
    val errorMessageResourceId: Int? = null
)