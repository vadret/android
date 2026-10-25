package fi.kroon.vadret.radar.wind

private const val OPAQUE = 0xFF
private const val ALPHA_SHIFT = 24
private const val RED_SHIFT = 16
private const val GREEN_SHIFT = 8

private class ColorStop(
    val windSpeed: Float,
    val red: Int,
    val green: Int,
    val blue: Int
)

private val colorStops: List<ColorStop> = listOf(
    ColorStop(windSpeed = 0f, red = 59, green = 70, blue = 160),
    ColorStop(windSpeed = 2f, red = 62, green = 110, blue = 190),
    ColorStop(windSpeed = 4f, red = 52, green = 150, blue = 175),
    ColorStop(windSpeed = 6f, red = 60, green = 170, blue = 120),
    ColorStop(windSpeed = 8f, red = 80, green = 180, blue = 70),
    ColorStop(windSpeed = 11f, red = 190, green = 175, blue = 70),
    ColorStop(windSpeed = 14f, red = 210, green = 130, blue = 70),
    ColorStop(windSpeed = 17f, red = 195, green = 75, blue = 120),
    ColorStop(windSpeed = 20f, red = 150, green = 60, blue = 170),
    ColorStop(windSpeed = 26f, red = 110, green = 55, blue = 160)
)

internal object WindColorScale {

    fun argb(windSpeed: Float): Int {
        val upperIndex: Int = colorStops.indexOfFirst { colorStop: ColorStop -> colorStop.windSpeed >= windSpeed }
        return when (upperIndex) {
            -1 -> colorStops.last().argb()

            0 -> colorStops.first().argb()

            else -> {
                val lower: ColorStop = colorStops[upperIndex - 1]
                val upper: ColorStop = colorStops[upperIndex]
                val fraction: Float = (windSpeed - lower.windSpeed) / (upper.windSpeed - lower.windSpeed)
                argb(
                    red = interpolate(from = lower.red, to = upper.red, fraction = fraction),
                    green = interpolate(from = lower.green, to = upper.green, fraction = fraction),
                    blue = interpolate(from = lower.blue, to = upper.blue, fraction = fraction)
                )
            }
        }
    }

    private fun ColorStop.argb(): Int =
        argb(
            red = red,
            green = green,
            blue = blue
        )

    private fun argb(
        red: Int,
        green: Int,
        blue: Int
    ): Int = (OPAQUE shl ALPHA_SHIFT) or (red shl RED_SHIFT) or (green shl GREEN_SHIFT) or blue

    private fun interpolate(
        from: Int,
        to: Int,
        fraction: Float
    ): Int = Math.round(from + (to - from) * fraction)
}