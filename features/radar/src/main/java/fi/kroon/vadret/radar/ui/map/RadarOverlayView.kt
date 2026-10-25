package fi.kroon.vadret.radar.ui.map

import android.content.Context
import android.graphics.Canvas
import android.view.View
import org.maplibre.android.maps.MapLibreMap

internal class RadarOverlayView(
    context: Context,
    private val windParticleDrawer: WindParticleDrawer,
    private val locationMarkerDrawer: LocationMarkerDrawer
) : View(context) {

    var map: MapLibreMap? = null
    var isWindShown: Boolean = false

    fun advanceWind() {
        val currentMap: MapLibreMap = map ?: return
        windParticleDrawer.advance(
            projection = currentMap.projection,
            screenWidth = width,
            screenHeight = height
        )
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        val currentMap: MapLibreMap = map ?: return
        if (isWindShown) {
            windParticleDrawer.draw(
                canvas = canvas,
                projection = currentMap.projection
            )
        }
        locationMarkerDrawer.draw(
            canvas = canvas,
            projection = currentMap.projection
        )
    }
}