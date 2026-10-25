package fi.kroon.vadret.radar.ui

import android.content.Context
import android.graphics.Bitmap
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import fi.kroon.vadret.location.model.Location
import fi.kroon.vadret.radar.model.RadarLayer
import fi.kroon.vadret.radar.ui.map.RadarMapView
import fi.kroon.vadret.radar.wind.WindAtLocation
import fi.kroon.vadret.radar.wind.WindField
import fi.kroon.vadret.ui.VadretTheme

private const val PARTICLE_FRAME_NANOS = 33_000_000L

@Composable
internal fun RadarMap(
    layer: RadarLayer,
    precipitationOverlay: Bitmap?,
    windField: WindField?,
    deviceLocation: Location?,
    windAtLocation: WindAtLocation?,
    windSpeedText: String?,
    modifier: Modifier = Modifier
) {
    val context: Context = LocalContext.current
    val markerColor: Int = VadretTheme.colorScheme.primary.toArgb()
    val labelTextColor: Int = VadretTheme.colors.onMapLabel.toArgb()
    val radarMapView: RadarMapView = remember(context, markerColor, labelTextColor) {
        RadarMapView(
            context = context,
            markerColor = markerColor,
            labelTextColor = labelTextColor
        )
    }
    MapLifecycleEffect(radarMapView = radarMapView)
    WindAnimationEffect(
        isAnimating = layer == RadarLayer.WIND && windField != null,
        radarMapView = radarMapView
    )

    AndroidView(
        factory = { radarMapView },
        update = { view: RadarMapView ->
            view.show(
                layer = layer,
                precipitationOverlay = precipitationOverlay,
                windField = windField,
                deviceLocation = deviceLocation,
                windAtLocation = windAtLocation,
                windSpeedText = windSpeedText
            )
        },
        modifier = modifier
    )
}

@Composable
private fun MapLifecycleEffect(radarMapView: RadarMapView) {
    val lifecycle: Lifecycle = LocalLifecycleOwner.current.lifecycle
    DisposableEffect(lifecycle, radarMapView) {
        val observer = LifecycleEventObserver { _, event: Lifecycle.Event ->
            when (event) {
                Lifecycle.Event.ON_START -> radarMapView.start()
                Lifecycle.Event.ON_RESUME -> radarMapView.resume()
                Lifecycle.Event.ON_PAUSE -> radarMapView.pause()
                Lifecycle.Event.ON_STOP -> radarMapView.stop()
                else -> Unit
            }
        }
        lifecycle.addObserver(observer)
        onDispose {
            lifecycle.removeObserver(observer)
            if (lifecycle.currentState.isAtLeast(state = Lifecycle.State.RESUMED)) {
                radarMapView.pause()
            }
            if (lifecycle.currentState.isAtLeast(state = Lifecycle.State.STARTED)) {
                radarMapView.stop()
            }
            radarMapView.destroy()
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