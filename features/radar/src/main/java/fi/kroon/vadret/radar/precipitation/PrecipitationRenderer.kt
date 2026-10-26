package fi.kroon.vadret.radar.precipitation

import android.graphics.Bitmap
import kotlin.time.Duration

internal interface PrecipitationRenderer {

    suspend fun render(echoes: Bitmap): PrecipitationOverlay

    suspend fun renderMotion(
        echoes: Bitmap,
        nextEchoes: Bitmap,
        duration: Duration
    ): PrecipitationOverlay
}