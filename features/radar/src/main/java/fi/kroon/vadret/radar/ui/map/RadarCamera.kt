package fi.kroon.vadret.radar.ui.map

import org.maplibre.android.camera.CameraPosition
import org.maplibre.android.geometry.LatLng

private const val INITIAL_LATITUDE = 58.62
private const val INITIAL_LONGITUDE = 15.57
private const val INITIAL_ZOOM_LEVEL = 5.0

internal data class RadarCamera(
    val latitude: Double,
    val longitude: Double,
    val zoom: Double
)

internal val initialRadarCamera: RadarCamera = RadarCamera(
    latitude = INITIAL_LATITUDE,
    longitude = INITIAL_LONGITUDE,
    zoom = INITIAL_ZOOM_LEVEL
)

internal fun RadarCamera.toCameraPosition(): CameraPosition =
    CameraPosition.Builder()
        .target(LatLng(latitude, longitude))
        .zoom(zoom)
        .build()

internal fun CameraPosition.toRadarCamera(): RadarCamera? {
    val cameraTarget: LatLng = target ?: return null
    return RadarCamera(
        latitude = cameraTarget.latitude,
        longitude = cameraTarget.longitude,
        zoom = zoom
    )
}