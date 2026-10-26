package fi.kroon.vadret.radar.precipitation

import android.graphics.Bitmap
import kotlin.time.Duration

internal class PrecipitationMotion(
    val nextEchoes: Bitmap,
    val field: Bitmap,
    val duration: Duration
)