package fi.kroon.vadret.radar.ui.map

import android.graphics.PointF
import fi.kroon.vadret.radar.projection.RADAR_EAST_LONGITUDE
import fi.kroon.vadret.radar.projection.RADAR_NORTH_LATITUDE
import fi.kroon.vadret.radar.projection.RADAR_SOUTH_LATITUDE
import fi.kroon.vadret.radar.projection.RADAR_WEST_LONGITUDE
import fi.kroon.vadret.radar.wind.RasterArea
import org.maplibre.android.geometry.LatLng
import org.maplibre.android.maps.Projection

internal class RasterProjection(
    private val originX: Float,
    private val originY: Float,
    private val scaleX: Float,
    private val scaleY: Float
) {

    val screenPixelsPerRasterPixel: Float = scaleX

    fun screenX(rasterX: Float): Float = originX + rasterX * scaleX

    fun screenY(rasterY: Float): Float = originY + rasterY * scaleY

    fun visibleArea(
        screenWidth: Int,
        screenHeight: Int,
        rasterWidth: Int,
        rasterHeight: Int
    ): RasterArea =
        RasterArea(
            left = ((0f - originX) / scaleX).coerceIn(minimumValue = 0f, maximumValue = rasterWidth.toFloat()),
            top = ((0f - originY) / scaleY).coerceIn(minimumValue = 0f, maximumValue = rasterHeight.toFloat()),
            right = ((screenWidth - originX) / scaleX).coerceIn(minimumValue = 0f, maximumValue = rasterWidth.toFloat()),
            bottom = ((screenHeight - originY) / scaleY).coerceIn(minimumValue = 0f, maximumValue = rasterHeight.toFloat())
        )
}

internal fun Projection.toRasterProjection(
    rasterWidth: Int,
    rasterHeight: Int
): RasterProjection {
    val northWest: PointF = toScreenLocation(LatLng(RADAR_NORTH_LATITUDE, RADAR_WEST_LONGITUDE))
    val southEast: PointF = toScreenLocation(LatLng(RADAR_SOUTH_LATITUDE, RADAR_EAST_LONGITUDE))
    return RasterProjection(
        originX = northWest.x,
        originY = northWest.y,
        scaleX = (southEast.x - northWest.x) / rasterWidth,
        scaleY = (southEast.y - northWest.y) / rasterHeight
    )
}