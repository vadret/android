package fi.kroon.vadret.radar.ui

import android.content.Context
import android.graphics.Bitmap
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import fi.kroon.vadret.radar.projection.RADAR_EAST_LONGITUDE
import fi.kroon.vadret.radar.projection.RADAR_NORTH_LATITUDE
import fi.kroon.vadret.radar.projection.RADAR_SOUTH_LATITUDE
import fi.kroon.vadret.radar.projection.RADAR_WEST_LONGITUDE
import org.osmdroid.config.Configuration
import org.osmdroid.config.IConfigurationProvider
import org.osmdroid.tileprovider.tilesource.XYTileSource
import org.osmdroid.util.BoundingBox
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.CustomZoomButtonsController
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.GroundOverlay
import java.io.File

private const val CENTER_LATITUDE = 58.62
private const val CENTER_LONGITUDE = 15.57
private const val INITIAL_ZOOM_LEVEL = 6.0
private const val MINIMUM_ZOOM_LEVEL = 5.0
private const val MAXIMUM_ZOOM_LEVEL = 20.0

private const val TILE_SOURCE_NAME = "wikimedia"
private const val TILE_FILE_EXTENSION = ".png"
private const val TILE_ZOOM_MIN_LEVEL = 1
private const val TILE_ZOOM_MAX_LEVEL = 18
private const val TILE_SIZE_PIXELS = 256
private const val OSMDROID_DIRECTORY = "osmdroid"
private const val TILE_CACHE_DIRECTORY = "tile"

@Composable
internal fun RadarMap(
    overlay: Bitmap?,
    modifier: Modifier = Modifier
) {
    val context: Context = LocalContext.current
    val mapView: MapView = remember(context) { createMapView(context = context) }
    val groundOverlay: GroundOverlay = remember {
        GroundOverlay().apply {
            setPosition(
                GeoPoint(RADAR_NORTH_LATITUDE, RADAR_WEST_LONGITUDE),
                GeoPoint(RADAR_SOUTH_LATITUDE, RADAR_EAST_LONGITUDE)
            )
        }
    }
    val lifecycle: Lifecycle = LocalLifecycleOwner.current.lifecycle
    DisposableEffect(lifecycle, mapView) {
        val observer = LifecycleEventObserver { _, event: Lifecycle.Event ->
            when (event) {
                Lifecycle.Event.ON_RESUME -> mapView.onResume()
                Lifecycle.Event.ON_PAUSE -> mapView.onPause()
                else -> Unit
            }
        }
        lifecycle.addObserver(observer)
        onDispose {
            lifecycle.removeObserver(observer)
            mapView.onDetach()
        }
    }

    AndroidView(
        factory = { mapView },
        update = { view: MapView ->
            if (overlay != null) {
                groundOverlay.image = overlay
                if (!view.overlays.contains(groundOverlay)) {
                    view.overlays.add(groundOverlay)
                }
                view.invalidate()
            }
        },
        modifier = modifier
    )
}

private fun createMapView(context: Context): MapView {
    val configuration: IConfigurationProvider = Configuration.getInstance()
    configuration.osmdroidBasePath = File(context.cacheDir, OSMDROID_DIRECTORY)
    configuration.osmdroidTileCache = File(configuration.osmdroidBasePath, TILE_CACHE_DIRECTORY)
    configuration.userAgentValue = context.packageName

    return MapView(context).apply {
        setTileSource(
            XYTileSource(
                TILE_SOURCE_NAME,
                TILE_ZOOM_MIN_LEVEL,
                TILE_ZOOM_MAX_LEVEL,
                TILE_SIZE_PIXELS,
                TILE_FILE_EXTENSION,
                arrayOf("https://maps.wikimedia.org/osm-intl/")
            )
        )
        isTilesScaledToDpi = true
        setMultiTouchControls(true)
        zoomController.setVisibility(CustomZoomButtonsController.Visibility.NEVER)
        maxZoomLevel = MAXIMUM_ZOOM_LEVEL
        minZoomLevel = MINIMUM_ZOOM_LEVEL
        controller.setCenter(GeoPoint(CENTER_LATITUDE, CENTER_LONGITUDE))
        controller.setZoom(INITIAL_ZOOM_LEVEL)
        setScrollableAreaLimitDouble(
            BoundingBox(
                RADAR_NORTH_LATITUDE,
                RADAR_EAST_LONGITUDE,
                RADAR_SOUTH_LATITUDE,
                RADAR_WEST_LONGITUDE
            )
        )
    }
}