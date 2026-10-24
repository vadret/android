package fi.kroon.vadret.radar

import android.graphics.Bitmap
import fi.kroon.vadret.common.Failure
import io.github.sphrak.either.Either

internal interface RadarImageLoader {

    /**
     *  A software bitmap, osmdroid draws it on its own canvas.
     */
    suspend fun load(imageUrl: String): Either<Failure, Bitmap>
}