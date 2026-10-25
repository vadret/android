package fi.kroon.vadret.radar.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import fi.kroon.vadret.core.R
import fi.kroon.vadret.di.koinViewModel
import fi.kroon.vadret.location.model.Location
import fi.kroon.vadret.radar.RadarEvent
import fi.kroon.vadret.radar.RadarState
import fi.kroon.vadret.radar.RadarViewModel
import fi.kroon.vadret.radar.model.RadarLayer
import fi.kroon.vadret.radar.wind.WindAtLocation
import fi.kroon.vadret.radar.wind.WindField
import fi.kroon.vadret.radar.wind.windAt
import fi.kroon.vadret.ui.VadretProgressBar
import fi.kroon.vadret.ui.VadretTheme
import java.util.Locale

private val toolbarTopPadding = 16.dp
private val attributionShape = RoundedCornerShape(size = 4.dp)
private val attributionPadding = PaddingValues(
    horizontal = 6.dp,
    vertical = 2.dp
)
private const val WIND_SPEED_FORMAT = "%.1f"
private const val COVER_FADE_MILLISECONDS = 250

@Composable
fun RadarRoute(
    modifier: Modifier = Modifier,
    viewModel: RadarViewModel = koinViewModel()
) {
    val state: RadarState by viewModel.state.collectAsStateWithLifecycle()
    RadarScreen(
        state = state,
        onEvent = viewModel::onEvent,
        modifier = modifier
    )
}

@Composable
fun RadarScreen(
    state: RadarState,
    onEvent: (RadarEvent) -> Unit,
    modifier: Modifier = Modifier
) {
    val snackbarHostState: SnackbarHostState = remember { SnackbarHostState() }
    val locale: Locale = LocalConfiguration.current.locales[0]
    LaunchedEffect(locale) {
        onEvent(RadarEvent.OnLocaleChanged(locale = locale))
    }
    LaunchedEffect(Unit) {
        onEvent(RadarEvent.OnStart)
    }
    val errorMessageResourceId: Int? = state.errorMessageResourceId
    if (errorMessageResourceId != null) {
        val message: String = stringResource(id = errorMessageResourceId)
        LaunchedEffect(errorMessageResourceId) {
            snackbarHostState.showSnackbar(message = message)
            onEvent(RadarEvent.OnErrorShown)
        }
    }
    val windAtLocation: WindAtLocation? = windAtDeviceLocation(
        layer = state.layer,
        windField = state.windField,
        deviceLocation = state.deviceLocation
    )

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        contentWindowInsets = WindowInsets(),
        modifier = modifier
    ) { innerPadding: PaddingValues ->
        Box(
            modifier = Modifier
                .padding(paddingValues = innerPadding)
                .fillMaxSize()
        ) {
            RadarMap(
                layer = state.layer,
                precipitationOverlay = state.precipitationOverlay,
                windField = state.windField,
                deviceLocation = state.deviceLocation,
                windAtLocation = windAtLocation,
                windSpeedText = windAtLocation?.windSpeedText(unit = stringResource(id = R.string.m_s)),
                onMapLoaded = { onEvent(RadarEvent.OnMapLoaded) },
                modifier = Modifier.fillMaxSize()
            )
            AnimatedVisibility(
                visible = state.isInitialLoading,
                enter = EnterTransition.None,
                exit = fadeOut(animationSpec = tween(durationMillis = COVER_FADE_MILLISECONDS)),
                modifier = Modifier.fillMaxSize()
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(color = VadretTheme.colorScheme.background)
                )
            }
            if (state.isLoading || state.isInitialLoading) {
                VadretProgressBar(
                    modifier = Modifier
                        .align(alignment = Alignment.TopCenter)
                        .statusBarsPadding()
                )
            }
            RadarLayerToolbar(
                selectedLayer = state.layer,
                onLayerSelected = { layer: RadarLayer -> onEvent(RadarEvent.OnLayerSelected(layer = layer)) },
                modifier = Modifier
                    .align(alignment = Alignment.TopCenter)
                    .statusBarsPadding()
                    .padding(top = toolbarTopPadding)
            )
            RadarBottomPanel(
                state = state,
                onEvent = onEvent,
                modifier = Modifier
                    .align(alignment = Alignment.BottomCenter)
                    .padding(all = VadretTheme.spacing.medium)
            )
        }
    }
}

@Composable
private fun RadarBottomPanel(
    state: RadarState,
    onEvent: (RadarEvent) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.End,
        verticalArrangement = Arrangement.spacedBy(space = VadretTheme.spacing.small),
        modifier = modifier.fillMaxWidth()
    ) {
        Text(
            text = stringResource(id = R.string.openstreetmap_copyright_notice),
            style = VadretTheme.textStyles.attribution,
            color = VadretTheme.colors.onMapAttribution,
            modifier = Modifier
                .background(
                    color = VadretTheme.colors.mapAttribution,
                    shape = attributionShape
                ).padding(paddingValues = attributionPadding)
        )
        val timeline: RadarTimeline? = radarTimeline(state = state)
        if (timeline != null) {
            RadarTimelineCard(
                layer = state.layer,
                timeline = timeline,
                isPlaying = state.isPlaying,
                onPlayClick = { onEvent(RadarEvent.OnPlayButtonClicked) },
                onIndexChange = { index: Int -> onEvent(RadarEvent.OnIndexChanged(index = index)) }
            )
        } else if (state.isInitialLoading) {
            RadarTimelinePlaceholder(layer = state.layer)
        }
    }
}

@Composable
private fun windAtDeviceLocation(
    layer: RadarLayer,
    windField: WindField?,
    deviceLocation: Location?
): WindAtLocation? =
    remember(layer, windField, deviceLocation) {
        if (layer == RadarLayer.WIND && windField != null && deviceLocation != null) {
            windField.values.windAt(
                latitude = deviceLocation.latitude,
                longitude = deviceLocation.longitude
            )
        } else {
            null
        }
    }

private fun WindAtLocation.windSpeedText(unit: String): String = "${String.format(Locale.ROOT, WIND_SPEED_FORMAT, windSpeed)} $unit"