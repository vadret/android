package fi.kroon.vadret.radar.ui.map

import fi.kroon.vadret.radar.projection.RADAR_EAST_LONGITUDE
import fi.kroon.vadret.radar.projection.RADAR_NORTH_LATITUDE
import fi.kroon.vadret.radar.projection.RADAR_SOUTH_LATITUDE
import fi.kroon.vadret.radar.projection.RADAR_WEST_LONGITUDE
import fi.kroon.vadret.radar.projection.WebMercator
import kotlin.math.PI
import kotlin.math.log2
import kotlin.math.max
import kotlin.math.pow

private const val TILE_SIZE = 512.0
private const val ZOOM_BASE = 2.0
private const val DEGREES_IN_CIRCLE = 360.0
private const val MERCATOR_WORLD_HEIGHT = 2 * PI
private const val HALF = 2.0

private val mercatorNorth: Double = WebMercator.y(latitude = RADAR_NORTH_LATITUDE)
private val mercatorSouth: Double = WebMercator.y(latitude = RADAR_SOUTH_LATITUDE)
private val mercatorCenter: Double = (mercatorNorth + mercatorSouth) / HALF
private const val LONGITUDE_SPAN: Double = RADAR_EAST_LONGITUDE - RADAR_WEST_LONGITUDE
private const val LONGITUDE_CENTER: Double = (RADAR_EAST_LONGITUDE + RADAR_WEST_LONGITUDE) / HALF

internal class RadarCameraLimits(
    val minimumZoom: Double,
    val northLatitude: Double,
    val eastLongitude: Double,
    val southLatitude: Double,
    val westLongitude: Double
)

internal fun radarCameraLimits(
    viewportWidth: Double,
    viewportHeight: Double,
    zoom: Double
): RadarCameraLimits {
    val minimumZoom: Double = max(
        a = log2(x = viewportWidth / (TILE_SIZE * LONGITUDE_SPAN / DEGREES_IN_CIRCLE)),
        b = log2(x = viewportHeight / (TILE_SIZE * (mercatorNorth - mercatorSouth) / MERCATOR_WORLD_HEIGHT))
    )
    val worldSize: Double = TILE_SIZE * ZOOM_BASE.pow(x = max(a = zoom, b = minimumZoom))
    val halfLongitudeSpan: Double = viewportWidth / HALF / worldSize * DEGREES_IN_CIRCLE
    val halfMercatorSpan: Double = viewportHeight / HALF / worldSize * MERCATOR_WORLD_HEIGHT
    return RadarCameraLimits(
        minimumZoom = minimumZoom,
        northLatitude = WebMercator.latitude(y = (mercatorNorth - halfMercatorSpan).coerceAtLeast(minimumValue = mercatorCenter)),
        eastLongitude = (RADAR_EAST_LONGITUDE - halfLongitudeSpan).coerceAtLeast(minimumValue = LONGITUDE_CENTER),
        southLatitude = WebMercator.latitude(y = (mercatorSouth + halfMercatorSpan).coerceAtMost(maximumValue = mercatorCenter)),
        westLongitude = (RADAR_WEST_LONGITUDE + halfLongitudeSpan).coerceAtMost(maximumValue = LONGITUDE_CENTER)
    )
}