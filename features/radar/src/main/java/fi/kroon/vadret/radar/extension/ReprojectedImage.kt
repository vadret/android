package fi.kroon.vadret.radar.extension

import android.graphics.Bitmap
import androidx.core.graphics.createBitmap
import fi.kroon.vadret.radar.projection.ReprojectedImage
import java.nio.ByteBuffer

internal fun ReprojectedImage.toEchoBitmap(): Bitmap {
    val bitmap: Bitmap = createBitmap(
        width = width,
        height = height,
        config = Bitmap.Config.ALPHA_8
    )
    val buffer: ByteBuffer = ByteBuffer.allocate(bitmap.rowBytes * height)
    for (row in 0 until height) {
        buffer.position(row * bitmap.rowBytes)
        buffer.put(
            echoes,
            row * width,
            width
        )
    }
    buffer.rewind()
    bitmap.copyPixelsFromBuffer(buffer)
    return bitmap
}