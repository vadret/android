package fi.kroon.vadret.radar.wind

import kotlin.math.atan2

data class WindAtLocation(
    val windSpeed: Float,
    val towardDegrees: Float
)

internal fun WindFieldValues.windAt(
    latitude: Double,
    longitude: Double
): WindAtLocation? {
    val pixel: Int = pixelAt(
        x = rasterX(longitude = longitude, width = width),
        y = rasterY(latitude = latitude, height = height)
    )
    val windSpeed: Float = valueAt(values = speeds, pixel = pixel)
    val eastward: Float = valueAt(values = eastward, pixel = pixel)
    val northward: Float = valueAt(values = northward, pixel = pixel)
    return if (windSpeed.isNaN() || eastward.isNaN() || northward.isNaN()) {
        null
    } else {
        WindAtLocation(
            windSpeed = windSpeed,
            towardDegrees = Math.toDegrees(atan2(eastward.toDouble(), northward.toDouble())).toFloat()
        )
    }
}