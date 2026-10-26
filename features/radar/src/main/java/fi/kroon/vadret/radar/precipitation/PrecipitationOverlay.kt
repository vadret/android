package fi.kroon.vadret.radar.precipitation

import android.graphics.Bitmap

internal sealed interface PrecipitationOverlay {

    class Colored(
        val bitmap: Bitmap
    ) : PrecipitationOverlay

    class Echoes(
        val echoes: Bitmap,
        val motion: PrecipitationMotion?
    ) : PrecipitationOverlay
}