package fi.kroon.vadret.radar.ui.map

import android.content.Context
import android.graphics.Canvas
import android.view.View
import org.maplibre.android.maps.MapLibreMap

internal class RadarOverlayView(
    context: Context,
    private val windParticles: WindParticleDrawer,
    private val locationMarker: LocationMarkerDrawer
) : View(context) {

    var map: MapLibreMap? = null
    var isWindShown: Boolean = false

    fun advanceWind() {
        val currentMap: MapLibreMap = map ?: return
        windParticles.advance(
            projection = currentMap.projection,
            screenWidth = width,
            screenHeight = height
        )
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        val currentMap: MapLibreMap = map ?: return
        if (isWindShown) {
            windParticles.draw(
                canvas = canvas,
                projection = currentMap.projection
            )
        }
        locationMarker.draw(
            canvas = canvas,
            projection = currentMap.projection
        )
    }
}