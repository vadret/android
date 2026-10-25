package fi.kroon.vadret.radar.ui.map

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.widget.FrameLayout
import fi.kroon.vadret.core.R
import fi.kroon.vadret.location.model.Location
import fi.kroon.vadret.radar.model.RadarLayer
import fi.kroon.vadret.radar.projection.RADAR_EAST_LONGITUDE
import fi.kroon.vadret.radar.projection.RADAR_NORTH_LATITUDE
import fi.kroon.vadret.radar.projection.RADAR_SOUTH_LATITUDE
import fi.kroon.vadret.radar.projection.RADAR_WEST_LONGITUDE
import fi.kroon.vadret.radar.wind.WindAtLocation
import fi.kroon.vadret.radar.wind.WindField
import org.maplibre.android.MapLibre
import org.maplibre.android.camera.CameraPosition
import org.maplibre.android.geometry.LatLng
import org.maplibre.android.geometry.LatLngBounds
import org.maplibre.android.maps.MapLibreMap
import org.maplibre.android.maps.MapView
import org.maplibre.android.maps.Style
import org.maplibre.android.style.layers.Layer
import org.maplibre.android.style.layers.SymbolLayer

private const val CENTER_LATITUDE = 58.62
private const val CENTER_LONGITUDE = 15.57
private const val INITIAL_ZOOM_LEVEL = 5.0
private const val MINIMUM_ZOOM_LEVEL = 4.0
private const val MAXIMUM_ZOOM_LEVEL = 12.0
private const val PRECIPITATION_LAYER_ID = "vadret-precipitation"
private const val PRECIPITATION_OPACITY = 1f
private const val WIND_LAYER_ID = "vadret-wind"
private const val WIND_OPACITY = 0.8f

internal class RadarMapView(
    context: Context,
    markerColor: Int,
    labelTextColor: Int
) : FrameLayout(context) {

    private val density: Float = resources.displayMetrics.density
    private val windParticles = WindParticleDrawer(density = density)
    private val locationMarker = LocationMarkerDrawer(
        density = density,
        scaledDensity = density * resources.configuration.fontScale,
        markerColor = markerColor,
        labelTextColor = labelTextColor,
        arrow = tintedArrow(context = context, color = markerColor)
    )
    private val overlayView = RadarOverlayView(
        context = context,
        windParticles = windParticles,
        locationMarker = locationMarker
    )
    private val mapView: MapView = createMapView(context = context)
    private var precipitationLayer: RadarImageLayer? = null
    private var windLayer: RadarImageLayer? = null
    private var layer: RadarLayer = RadarLayer.PRECIPITATION
    private var precipitationOverlay: Bitmap? = null
    private var windField: WindField? = null

    init {
        addView(mapView)
        addView(overlayView)
        mapView.getMapAsync { map: MapLibreMap -> onMapReady(map = map) }
    }

    fun show(
        layer: RadarLayer,
        precipitationOverlay: Bitmap?,
        windField: WindField?,
        deviceLocation: Location?,
        windAtLocation: WindAtLocation?,
        windSpeedText: String?
    ) {
        this.layer = layer
        this.precipitationOverlay = precipitationOverlay
        this.windField = windField
        windParticles.windField = windField
        locationMarker.location = deviceLocation
        locationMarker.windAtLocation = windAtLocation
        locationMarker.windSpeedText = windSpeedText
        overlayView.isWindShown = layer == RadarLayer.WIND
        showImageLayers()
        overlayView.invalidate()
    }

    fun advanceWind() {
        overlayView.advanceWind()
    }

    fun start() {
        mapView.onStart()
    }

    fun resume() {
        mapView.onResume()
    }

    fun pause() {
        mapView.onPause()
    }

    fun stop() {
        mapView.onStop()
    }

    fun destroy() {
        mapView.onDestroy()
    }

    private fun onMapReady(map: MapLibreMap) {
        configure(map = map)
        overlayView.map = map
        map.addOnCameraMoveListener { overlayView.invalidate() }
        map.setStyle(Style.Builder().fromUri("https://tiles.openfreemap.org/styles/positron")) { style: Style ->
            style.localizeLabels(language = resources.configuration.locales[0].language)
            val labelLayerId: String? = style.layers.firstOrNull { styleLayer: Layer -> styleLayer is SymbolLayer }?.id
            precipitationLayer = RadarImageLayer(
                style = style,
                layerId = PRECIPITATION_LAYER_ID,
                opacity = PRECIPITATION_OPACITY,
                belowLayerId = labelLayerId
            )
            windLayer = RadarImageLayer(
                style = style,
                layerId = WIND_LAYER_ID,
                opacity = WIND_OPACITY,
                belowLayerId = labelLayerId
            )
            showImageLayers()
            overlayView.invalidate()
        }
    }

    private fun showImageLayers() {
        val isWind: Boolean = layer == RadarLayer.WIND
        precipitationLayer?.show(
            bitmap = precipitationOverlay,
            isVisible = !isWind
        )
        windLayer?.show(
            bitmap = windField?.bitmap,
            isVisible = isWind
        )
    }

    private fun configure(map: MapLibreMap) {
        map.uiSettings.isRotateGesturesEnabled = false
        map.uiSettings.isTiltGesturesEnabled = false
        map.uiSettings.isCompassEnabled = false
        map.uiSettings.isLogoEnabled = false
        map.uiSettings.isAttributionEnabled = false
        map.setMinZoomPreference(MINIMUM_ZOOM_LEVEL)
        map.setMaxZoomPreference(MAXIMUM_ZOOM_LEVEL)
        map.setLatLngBoundsForCameraTarget(
            LatLngBounds.from(RADAR_NORTH_LATITUDE, RADAR_EAST_LONGITUDE, RADAR_SOUTH_LATITUDE, RADAR_WEST_LONGITUDE)
        )
        map.cameraPosition = CameraPosition.Builder()
            .target(LatLng(CENTER_LATITUDE, CENTER_LONGITUDE))
            .zoom(INITIAL_ZOOM_LEVEL)
            .build()
    }
}

private fun createMapView(context: Context): MapView {
    MapLibre.getInstance(context)
    val mapView = MapView(context)
    mapView.onCreate(null)
    return mapView
}

private fun tintedArrow(
    context: Context,
    color: Int
): Drawable {
    val arrow: Drawable = requireNotNull(context.getDrawable(R.drawable.ic_north)).mutate()
    arrow.setTint(color)
    return arrow
}