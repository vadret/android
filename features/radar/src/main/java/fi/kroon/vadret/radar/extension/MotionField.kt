package fi.kroon.vadret.radar.extension

import android.graphics.Bitmap
import fi.kroon.vadret.radar.precipitation.MAXIMUM_ECHO_DISPLACEMENT
import fi.kroon.vadret.radar.precipitation.MotionField
import kotlin.math.roundToInt

private const val DISPLACEMENT_CHANNEL_RANGE = 127f
private const val CHANNEL_MINIMUM = 0
private const val CHANNEL_MAXIMUM = 0xFF
private const val ALPHA_SHIFT = 24
private const val RED_SHIFT = 16
private const val GREEN_SHIFT = 8
private const val OPAQUE = CHANNEL_MAXIMUM shl ALPHA_SHIFT

internal const val NEUTRAL_DISPLACEMENT_CHANNEL = 128
internal const val DISPLACEMENT_PER_CHANNEL_STEP = MAXIMUM_ECHO_DISPLACEMENT / DISPLACEMENT_CHANNEL_RANGE

internal fun MotionField.toBitmap(): Bitmap =
    Bitmap.createBitmap(
        IntArray(size = columns * rows) { cellIndex: Int ->
            OPAQUE or
                (eastward[cellIndex].toChannel() shl RED_SHIFT) or
                (southward[cellIndex].toChannel() shl GREEN_SHIFT)
        },
        columns,
        rows,
        Bitmap.Config.ARGB_8888
    )

private fun Float.toChannel(): Int =
    (NEUTRAL_DISPLACEMENT_CHANNEL + this / DISPLACEMENT_PER_CHANNEL_STEP)
        .roundToInt()
        .coerceIn(
            minimumValue = CHANNEL_MINIMUM,
            maximumValue = CHANNEL_MAXIMUM
        )