package fi.kroon.vadret.radar.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import fi.kroon.vadret.core.R
import fi.kroon.vadret.di.koinViewModel
import fi.kroon.vadret.radar.RadarEvent
import fi.kroon.vadret.radar.RadarState
import fi.kroon.vadret.radar.RadarViewModel
import fi.kroon.vadret.radar.model.RadarFrame
import fi.kroon.vadret.ui.VadretProgressBar
import fi.kroon.vadret.ui.VadretTheme
import fi.kroon.vadret.ui.VadretTopAppBar
import kotlin.math.roundToInt

private const val CONTROLS_ALPHA = 0.85f

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

    Scaffold(
        topBar = { VadretTopAppBar() },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        modifier = modifier
    ) { innerPadding: PaddingValues ->
        Box(
            modifier = Modifier
                .padding(paddingValues = innerPadding)
                .fillMaxSize()
        ) {
            RadarMap(
                overlay = state.overlay,
                modifier = Modifier.fillMaxSize()
            )
            if (state.isLoading) {
                VadretProgressBar(modifier = Modifier.align(alignment = Alignment.TopCenter))
            }
            if (state.frames.isNotEmpty()) {
                PlaybackControls(
                    state = state,
                    onEvent = onEvent,
                    modifier = Modifier
                        .align(alignment = Alignment.BottomCenter)
                        .padding(all = VadretTheme.spacing.large)
                )
            }
        }
    }
}

@Composable
private fun PlaybackControls(
    state: RadarState,
    onEvent: (RadarEvent) -> Unit,
    modifier: Modifier = Modifier
) {
    val frame: RadarFrame? = state.frames.getOrNull(state.index)
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier.fillMaxWidth()
    ) {
        FloatingActionButton(
            onClick = { onEvent(RadarEvent.OnPlayButtonClicked) },
            shape = CircleShape,
            containerColor = VadretTheme.colorScheme.primary,
            contentColor = VadretTheme.colorScheme.onPrimary
        ) {
            Icon(
                painter = painterResource(id = playButtonIconResourceId(isPlaying = state.isPlaying)),
                contentDescription = null
            )
        }
        Surface(
            shape = VadretTheme.shapes.large,
            color = VadretTheme.colorScheme.surface.copy(alpha = CONTROLS_ALPHA),
            modifier = Modifier
                .weight(weight = 1f)
                .padding(start = VadretTheme.spacing.medium)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(horizontal = VadretTheme.spacing.medium)
            ) {
                RadarSlider(
                    value = state.index.toFloat(),
                    onValueChange = { value: Float -> onEvent(RadarEvent.OnIndexChanged(index = value.roundToInt())) },
                    valueRange = 0f..state.frames.lastIndex.coerceAtLeast(minimumValue = 1).toFloat(),
                    modifier = Modifier.weight(weight = 1f)
                )
                if (frame != null) {
                    Text(
                        text = frame.timeText,
                        style = VadretTheme.typography.labelLarge,
                        modifier = Modifier.padding(start = VadretTheme.spacing.medium)
                    )
                }
            }
        }
    }
}

private fun playButtonIconResourceId(isPlaying: Boolean): Int =
    if (isPlaying) {
        R.drawable.ic_pause_white_24dp
    } else {
        R.drawable.ic_play_arrow_white_24dp
    }