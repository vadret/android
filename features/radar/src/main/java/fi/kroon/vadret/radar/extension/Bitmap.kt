package fi.kroon.vadret.radar.extension

import android.graphics.Bitmap
import java.nio.ByteBuffer

private const val BYTE_MASK = 0xFF

internal fun Bitmap.echoLevels(): ByteArray {
    val buffer: ByteBuffer = ByteBuffer.allocate(rowBytes * height)
    copyPixelsToBuffer(buffer)
    val echoLevels = ByteArray(size = width * height)
    for (row in 0 until height) {
        buffer.position(row * rowBytes)
        buffer.get(
            echoLevels,
            row * width,
            width
        )
    }
    return echoLevels
}

internal fun Bitmap.colored(palette: IntArray): Bitmap {
    val echoLevels: ByteArray = echoLevels()
    return Bitmap.createBitmap(
        IntArray(size = echoLevels.size) { index: Int -> palette[echoLevels[index].toInt() and BYTE_MASK] },
        width,
        height,
        Bitmap.Config.ARGB_8888
    )
}