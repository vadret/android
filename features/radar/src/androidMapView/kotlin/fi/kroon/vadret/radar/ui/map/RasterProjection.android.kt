package fi.kroon.vadret.radar.ui.map

import android.graphics.PointF
import fi.kroon.vadret.radar.projection.RADAR_EAST_LONGITUDE
import fi.kroon.vadret.radar.projection.RADAR_NORTH_LATITUDE
import fi.kroon.vadret.radar.projection.RADAR_SOUTH_LATITUDE
import fi.kroon.vadret.radar.projection.RADAR_WEST_LONGITUDE
import org.maplibre.android.geometry.LatLng
import org.maplibre.android.maps.Projection

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