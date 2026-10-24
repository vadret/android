package fi.kroon.vadret.radar

import android.graphics.Bitmap
import androidx.annotation.StringRes
import fi.kroon.vadret.radar.model.RadarFrame
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

data class RadarState(
    val frames: ImmutableList<RadarFrame> = persistentListOf(),
    val index: Int = 0,
    val overlay: Bitmap? = null,
    val isPlaying: Boolean = false,
    val isLoading: Boolean = true,
    @StringRes
    val errorMessageResourceId: Int? = null
)