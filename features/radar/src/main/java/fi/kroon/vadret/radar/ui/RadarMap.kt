package fi.kroon.vadret.radar.ui

import android.graphics.Bitmap
import android.view.ViewGroup
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.viewinterop.AndroidView
import fi.kroon.vadret.location.model.Location
import fi.kroon.vadret.radar.model.RadarLayer
import fi.kroon.vadret.radar.ui.map.RadarMapAppearance
import fi.kroon.vadret.radar.ui.map.RadarMapStore
import fi.kroon.vadret.radar.ui.map.RadarMapView
import fi.kroon.vadret.radar.wind.WindAtLocation
import fi.kroon.vadret.radar.wind.WindField
import fi.kroon.vadret.ui.VadretTheme

private const val PARTICLE_FRAME_NANOS = 33_000_000L
private const val DAY_PARTICLE_COLOR = 0xD9FFFFFF.toInt()
private const val NIGHT_PARTICLE_COLOR = 0xB3D2DCFF.toInt()

@Composable
internal fun RadarMap(
    layer: RadarLayer,
    precipitationOverlay: Bitmap?,
    windField: WindField?,
    deviceLocation: Location?,
    windAtLocation: WindAtLocation?,
    windSpeedText: String?,
    onMapLoaded: () -> Unit,
    modifier: Modifier = Modifier
) {
    val radarMapStore: RadarMapStore = LocalRadarMapStore.current
    val isDark: Boolean = VadretTheme.isDark
    val radarMapAppearance = RadarMapAppearance(
        isDark = isDark,
        markerColor = VadretTheme.colorScheme.primary.toArgb(),
        surfaceColor = VadretTheme.colorScheme.surface.toArgb(),
        labelTextColor = VadretTheme.colorScheme.onSurface.toArgb(),
        particleColor = if (isDark) {
            NIGHT_PARTICLE_COLOR
        } else {
            DAY_PARTICLE_COLOR
        }
    )
    val currentOnMapLoaded: () -> Unit by rememberUpdatedState(newValue = onMapLoaded)
    val radarMapView: RadarMapView = remember(radarMapStore) { radarMapStore.radarMapView() }
    MapShownEffect(
        radarMapStore = radarMapStore,
        radarMapView = radarMapView,
        onMapLoaded = { currentOnMapLoaded() }
    )
    WindAnimationEffect(
        isAnimating = layer == RadarLayer.WIND && windField != null,
        radarMapView = radarMapView
    )

    AndroidView(
        factory = { radarMapView },
        update = { view: RadarMapView ->
            view.setAppearance(radarMapAppearance = radarMapAppearance)
            view.show(
                layer = layer,
                precipitationOverlay = precipitationOverlay,
                windField = windField,
                deviceLocation = deviceLocation,
                windAtLocation = windAtLocation,
                windSpeedText = windSpeedText
            )
        },
        onRelease = { view: RadarMapView ->
            val parent: ViewGroup? = view.parent as? ViewGroup
            parent?.removeView(view)
        },
        modifier = modifier
    )
}

@Composable
private fun MapShownEffect(
    radarMapStore: RadarMapStore,
    radarMapView: RadarMapView,
    onMapLoaded: () -> Unit
) {
    DisposableEffect(radarMapStore, radarMapView) {
        radarMapView.setOnMapLoadedListener(onMapLoaded = onMapLoaded)
        radarMapStore.onShownChanged(isShown = true)
        onDispose {
            radarMapView.setOnMapLoadedListener(onMapLoaded = null)
            radarMapStore.onShownChanged(isShown = false)
        }
    }
}

@Composable
private fun WindAnimationEffect(
    isAnimating: Boolean,
    radarMapView: RadarMapView
) {
    LaunchedEffect(isAnimating, radarMapView) {
        var lastFrameNanos = 0L
        while (isAnimating) {
            withFrameNanos { frameNanos: Long ->
                if (frameNanos - lastFrameNanos >= PARTICLE_FRAME_NANOS) {
                    lastFrameNanos = frameNanos
                    radarMapView.advanceWind()
                }
            }
        }
    }
}