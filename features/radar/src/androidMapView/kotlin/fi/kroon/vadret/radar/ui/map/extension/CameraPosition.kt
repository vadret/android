package fi.kroon.vadret.radar.ui.map.extension

import fi.kroon.vadret.radar.ui.map.RadarCamera
import org.maplibre.android.camera.CameraPosition
import org.maplibre.android.geometry.LatLng

internal fun CameraPosition.toRadarCamera(): RadarCamera? {
    val cameraTarget: LatLng = target ?: return null
    return RadarCamera(
        latitude = cameraTarget.latitude,
        longitude = cameraTarget.longitude,
        zoom = zoom
    )
}