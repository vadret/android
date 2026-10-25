package fi.kroon.vadret.radar.ui.map

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.widget.FrameLayout
import androidx.lifecycle.Lifecycle
import fi.kroon.vadret.core.R
import fi.kroon.vadret.location.model.Location
import fi.kroon.vadret.radar.model.RadarLayer
import fi.kroon.vadret.radar.projection.RADAR_EAST_LONGITUDE
import fi.kroon.vadret.radar.projection.RADAR_NORTH_LATITUDE
import fi.kroon.vadret.radar.projection.RADAR_SOUTH_LATITUDE
import fi.kroon.vadret.radar.projection.RADAR_WEST_LONGITUDE
import fi.kroon.vadret.radar.ui.map.extension.toCameraPosition
import fi.kroon.vadret.radar.ui.map.extension.toRadarCamera
import fi.kroon.vadret.radar.wind.WindAtLocation
import fi.kroon.vadret.radar.wind.WindField
import org.maplibre.android.MapLibre
import org.maplibre.android.geometry.LatLngBounds
import org.maplibre.android.maps.MapLibreMap
import org.maplibre.android.maps.MapView
import org.maplibre.android.maps.Style
import org.maplibre.android.style.layers.Layer
import org.maplibre.android.style.layers.PropertyFactory
import org.maplibre.android.style.layers.SymbolLayer

private const val MINIMUM_ZOOM_LEVEL = 4.0
private const val MAXIMUM_ZOOM_LEVEL = 12.0
private const val PRECIPITATION_LAYER_ID = "vadret-precipitation"
private const val PRECIPITATION_OPACITY = 1f
private const val WIND_LAYER_ID = "vadret-wind"
private const val WIND_OPACITY = 0.8f
private const val BACKGROUND_LAYER_ID = "background"
private const val WATER_LAYER_ID = "water"
private const val DARK_LAND_COLOR = "#181512"
private const val DARK_WATER_COLOR = "#21364B"
private const val DARK_LABEL_COLOR = "#FFFFFF"

internal class RadarMapView(
    context: Context,
    private val initialCamera: RadarCamera,
    private val onCameraIdle: (RadarCamera) -> Unit
) : FrameLayout(context) {

    private val density: Float = resources.displayMetrics.density
    private val windParticleDrawer = WindParticleDrawer(density = density)
    private val locationMarkerDrawer = LocationMarkerDrawer(
        density = density,
        scaledDensity = density * resources.configuration.fontScale,
        arrow = mutableArrow(context = context)
    )
    private val overlayView = RadarOverlayView(
        context = context,
        windParticleDrawer = windParticleDrawer,
        locationMarkerDrawer = locationMarkerDrawer
    )
    private val mapView: MapView = createMapView(context = context)
    private var precipitationLayer: RadarImageLayer? = null
    private var windLayer: RadarImageLayer? = null
    private var layer: RadarLayer = RadarLayer.PRECIPITATION
    private var precipitationOverlay: Bitmap? = null
    private var windField: WindField? = null
    private var lifecycleState: Lifecycle.State = Lifecycle.State.CREATED
    private var isMapLoaded: Boolean = false
    private var onMapLoaded: (() -> Unit)? = null
    private var map: MapLibreMap? = null
    private var isDark: Boolean = false

    init {
        addView(mapView)
        addView(overlayView)
        mapView.addOnDidFinishLoadingMapListener { markMapLoaded() }
        mapView.addOnDidFailLoadingMapListener { _: String? -> markMapLoaded() }
        mapView.getMapAsync { map: MapLibreMap -> onMapReady(map = map) }
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        post { map?.triggerRepaint() }
    }

    fun setOnMapLoadedListener(onMapLoaded: (() -> Unit)?) {
        this.onMapLoaded = onMapLoaded
        if (isMapLoaded && onMapLoaded != null) {
            onMapLoaded()
        }
    }

    fun setAppearance(radarMapAppearance: RadarMapAppearance) {
        locationMarkerDrawer.setColors(
            markerColor = radarMapAppearance.markerColor,
            surfaceColor = radarMapAppearance.surfaceColor,
            labelTextColor = radarMapAppearance.labelTextColor
        )
        windParticleDrawer.particleColor = radarMapAppearance.particleColor
        if (radarMapAppearance.isDark != isDark) {
            isDark = radarMapAppearance.isDark
            val readyMap: MapLibreMap? = map
            if (readyMap != null) {
                loadStyle(map = readyMap)
            }
        }
        overlayView.invalidate()
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
        windParticleDrawer.windField = windField
        locationMarkerDrawer.location = deviceLocation
        locationMarkerDrawer.windAtLocation = windAtLocation
        locationMarkerDrawer.windSpeedText = windSpeedText
        overlayView.isWindShown = layer == RadarLayer.WIND
        showImageLayers()
        overlayView.invalidate()
    }

    fun advanceWind() {
        overlayView.advanceWind()
    }

    fun moveTo(state: Lifecycle.State) {
        if (lifecycleState < Lifecycle.State.STARTED && state >= Lifecycle.State.STARTED) {
            mapView.onStart()
            lifecycleState = Lifecycle.State.STARTED
        }
        if (lifecycleState < Lifecycle.State.RESUMED && state >= Lifecycle.State.RESUMED) {
            mapView.onResume()
            lifecycleState = Lifecycle.State.RESUMED
        }
        if (lifecycleState == Lifecycle.State.RESUMED && state < Lifecycle.State.RESUMED) {
            mapView.onPause()
            lifecycleState = Lifecycle.State.STARTED
        }
        if (lifecycleState == Lifecycle.State.STARTED && state < Lifecycle.State.STARTED) {
            mapView.onStop()
            lifecycleState = Lifecycle.State.CREATED
        }
    }

    fun destroy() {
        moveTo(state = Lifecycle.State.CREATED)
        mapView.onDestroy()
    }

    private fun markMapLoaded() {
        isMapLoaded = true
        onMapLoaded?.invoke()
    }

    private fun onMapReady(map: MapLibreMap) {
        configure(map = map)
        this.map = map
        overlayView.map = map
        map.addOnCameraMoveListener { overlayView.invalidate() }
        map.addOnCameraIdleListener { reportCameraIdle(map = map) }
        loadStyle(map = map)
    }

    private fun loadStyle(map: MapLibreMap) {
        precipitationLayer = null
        windLayer = null
        val styleUri: String = if (isDark) {
            "https://tiles.openfreemap.org/styles/dark"
        } else {
            "https://tiles.openfreemap.org/styles/positron"
        }
        map.setStyle(Style.Builder().fromUri(styleUri)) { style: Style ->
            style.localizeLabels(language = resources.configuration.locales[0].language)
            if (isDark) {
                style.useDarkMapColors()
            }
            val labelLayerId: String? = style.labelLayerAboveBaseMap()
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
        map.cameraPosition = initialCamera.toCameraPosition()
    }

    private fun reportCameraIdle(map: MapLibreMap) {
        val radarCamera: RadarCamera = map.cameraPosition.toRadarCamera() ?: return
        onCameraIdle(radarCamera)
    }
}

private fun Style.labelLayerAboveBaseMap(): String? {
    val styleLayers: List<Layer> = layers
    val lastBaseMapIndex: Int = styleLayers.indexOfLast { styleLayer: Layer -> styleLayer !is SymbolLayer }
    return styleLayers.getOrNull(index = lastBaseMapIndex + 1)?.id
}

private fun Style.useDarkMapColors() {
    getLayer(BACKGROUND_LAYER_ID)?.setProperties(PropertyFactory.backgroundColor(DARK_LAND_COLOR))
    getLayer(WATER_LAYER_ID)?.setProperties(PropertyFactory.fillColor(DARK_WATER_COLOR))
    layers
        .filterIsInstance<SymbolLayer>()
        .forEach { symbolLayer: SymbolLayer -> symbolLayer.setProperties(PropertyFactory.textColor(DARK_LABEL_COLOR)) }
}

private fun createMapView(context: Context): MapView {
    MapLibre.getInstance(context)
    val mapView = MapView(context)
    mapView.onCreate(null)
    return mapView
}

private fun mutableArrow(context: Context): Drawable = requireNotNull(context.getDrawable(R.drawable.ic_north)).mutate()