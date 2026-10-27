package fi.kroon.vadret.radar.ui.map.extension

import fi.kroon.vadret.radar.ui.map.RadarCamera
import org.maplibre.android.camera.CameraPosition
import org.maplibre.android.geometry.LatLng

internal fun RadarCamera.toCameraPosition(): CameraPosition =
    CameraPosition.Builder()
        .target(LatLng(latitude, longitude))
        .zoom(zoom)
        .build()