package fi.kroon.vadret.radar

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import fi.kroon.vadret.common.Failure
import fi.kroon.vadret.location.model.Location
import fi.kroon.vadret.radar.extension.toRadarFrames
import fi.kroon.vadret.radar.extension.toWindTimeFrames
import fi.kroon.vadret.radar.model.Radar
import fi.kroon.vadret.radar.model.RadarFrame
import fi.kroon.vadret.radar.model.RadarLayer
import fi.kroon.vadret.radar.model.WindTimeFrame
import fi.kroon.vadret.radar.wind.WindField
import fi.kroon.vadret.radar.wind.WindFieldRenderer
import fi.kroon.vadret.radar.wind.WindPalette
import fi.kroon.vadret.ui.extension.launchReplacing
import fi.kroon.vadret.ui.extension.messageResourceId
import fi.kroon.vadret.wind.model.WindForecast
import fi.kroon.vadret.wind.model.WindFrame
import io.github.sphrak.either.extension.onResult
import io.github.sphrak.either.extension.successOrNull
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.flow.updateAndGet
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.datetime.TimeZone
import java.util.Locale
import kotlin.concurrent.atomics.AtomicBoolean
import kotlin.concurrent.atomics.AtomicReference
import kotlin.time.Clock
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds

private val precipitationFrameDuration: Duration = 256.milliseconds
private val windFrameDuration: Duration = 600.milliseconds

class RadarViewModel internal constructor(
    private val getRadarTask: GetRadarTask,
    private val getWindForecastTask: GetWindForecastTask,
    private val getDeviceLocationTask: GetDeviceLocationTask,
    private val radarImageLoader: RadarImageLoader,
    private val windFieldRenderer: WindFieldRenderer,
    private val clock: Clock,
    private val coroutineDispatcher: CoroutineDispatcher
) : ViewModel() {

    internal val state: StateFlow<RadarState>
        field = MutableStateFlow(value = RadarState())

    private val isStarted: AtomicBoolean = AtomicBoolean(value = false)
    private val isWindRequested: AtomicBoolean = AtomicBoolean(value = false)
    private val locale: AtomicReference<Locale> = AtomicReference(value = Locale.getDefault())
    private val windPalette: AtomicReference<WindPalette> = AtomicReference(value = WindPalette.DAY)
    private val radar: AtomicReference<Radar?> = AtomicReference(value = null)
    private val windForecast: AtomicReference<WindForecast?> = AtomicReference(value = null)
    private val playJob: AtomicReference<Job?> = AtomicReference(value = null)
    private val frameJob: AtomicReference<Job?> = AtomicReference(value = null)

    internal fun onEvent(event: RadarEvent) {
        when (event) {
            RadarEvent.OnStart -> onStart()

            is RadarEvent.OnLocaleChanged -> onLocaleChanged(locale = event.locale)

            is RadarEvent.OnDarkThemeChanged -> onDarkThemeChanged(isDarkTheme = event.isDarkTheme)

            RadarEvent.OnMapLoaded -> state.update { state: RadarState ->
                state.copy(isMapLoaded = true)
            }

            RadarEvent.OnPlayButtonClicked -> onPlayButtonClicked()

            RadarEvent.OnErrorShown -> state.update { state: RadarState ->
                state.copy(errorMessageResourceId = null)
            }

            is RadarEvent.OnIndexChanged -> onIndexChanged(index = event.index)

            is RadarEvent.OnLayerSelected -> onLayerSelected(layer = event.layer)
        }
    }

    private fun onStart() {
        if (isStarted.compareAndSet(expectedValue = false, newValue = true)) {
            loadPrecipitation()
            loadDeviceLocation()
        }
    }

    private fun loadPrecipitation() {
        viewModelScope.launch(context = coroutineDispatcher) {
            getRadarTask()
                .onResult(
                    onError = { failure: Failure -> displayPrecipitationError(failure = failure) },
                    onSuccess = { loadedRadar: Radar ->
                        radar.store(newValue = loadedRadar)
                        val updatedState: RadarState = state.updateAndGet { state: RadarState ->
                            val frames: ImmutableList<RadarFrame> = loadedRadar.toFrames()
                            state.copy(
                                precipitationFrames = frames,
                                precipitationIndex = frames.lastIndex.coerceAtLeast(minimumValue = 0),
                                isPrecipitationUnavailable = frames.isEmpty(),
                                isLoading = state.layer == RadarLayer.WIND && state.windField == null
                            )
                        }
                        showFrame(
                            layer = RadarLayer.PRECIPITATION,
                            index = updatedState.precipitationFrames.lastIndex
                        )
                    }
                )
        }
    }

    private fun onLocaleChanged(locale: Locale) {
        if (this.locale.exchange(newValue = locale) == locale) {
            return
        }
        viewModelScope.launch(context = coroutineDispatcher) {
            state.update { state: RadarState ->
                state.copy(
                    precipitationFrames = radar.load()?.toFrames() ?: state.precipitationFrames,
                    windFrames = windForecast.load()?.toTimeFrames() ?: state.windFrames
                )
            }
        }
    }

    private fun onDarkThemeChanged(isDarkTheme: Boolean) {
        val palette: WindPalette = if (isDarkTheme) {
            WindPalette.NIGHT
        } else {
            WindPalette.DAY
        }
        if (windPalette.exchange(newValue = palette) == palette || state.value.windField == null) {
            return
        }
        viewModelScope.launch(context = coroutineDispatcher) {
            loadWindFrame(index = state.value.windIndex)
        }
    }

    private fun Radar.toFrames(): ImmutableList<RadarFrame> =
        toRadarFrames(
            now = clock.now(),
            timeZone = TimeZone.currentSystemDefault(),
            locale = locale.load()
        ).toImmutableList()

    private fun WindForecast.toTimeFrames(): ImmutableList<WindTimeFrame> =
        toWindTimeFrames(
            timeZone = TimeZone.currentSystemDefault(),
            locale = locale.load()
        ).toImmutableList()

    private fun loadDeviceLocation() {
        viewModelScope.launch(context = coroutineDispatcher) {
            val location: Location? = getDeviceLocationTask().successOrNull
            if (location != null) {
                state.update { state: RadarState ->
                    state.copy(deviceLocation = location)
                }
            }
        }
    }

    private fun onLayerSelected(layer: RadarLayer) {
        if (layer == state.value.layer) {
            return
        }
        stopPlaying()
        frameJob.exchange(newValue = null)?.cancel()
        state.update { state: RadarState ->
            state.copy(layer = layer)
        }
        if (layer == RadarLayer.WIND && isWindRequested.compareAndSet(expectedValue = false, newValue = true)) {
            loadWind()
        }
    }

    private fun loadWind() {
        state.update { state: RadarState ->
            state.copy(isLoading = true)
        }
        viewModelScope.launch(context = coroutineDispatcher) {
            getWindForecastTask()
                .onResult(
                    onError = { failure: Failure ->
                        isWindRequested.store(newValue = false)
                        displayError(failure = failure)
                    },
                    onSuccess = { forecast: WindForecast ->
                        windForecast.store(newValue = forecast)
                        state.update { state: RadarState ->
                            state.copy(
                                windFrames = forecast.toTimeFrames(),
                                windIndex = 0
                            )
                        }
                        showFrame(
                            layer = RadarLayer.WIND,
                            index = 0
                        )
                    }
                )
        }
    }

    private fun onIndexChanged(index: Int) {
        stopPlaying()
        val layer: RadarLayer = state.value.layer
        state.update { state: RadarState ->
            when (layer) {
                RadarLayer.PRECIPITATION -> state.copy(precipitationIndex = index)
                RadarLayer.WIND -> state.copy(windIndex = index)
            }
        }
        showFrame(
            layer = layer,
            index = index
        )
    }

    private fun showFrame(
        layer: RadarLayer,
        index: Int
    ) {
        viewModelScope.launchReplacing(
            currentJob = frameJob,
            context = coroutineDispatcher
        ) {
            loadFrame(
                layer = layer,
                index = index
            )
        }
    }

    private suspend fun loadFrame(
        layer: RadarLayer,
        index: Int
    ): Boolean =
        when (layer) {
            RadarLayer.PRECIPITATION -> loadPrecipitationFrame(index = index)
            RadarLayer.WIND -> loadWindFrame(index = index)
        }

    private suspend fun loadPrecipitationFrame(index: Int): Boolean {
        val frame: RadarFrame = state.value.precipitationFrames.getOrNull(index = index) ?: return false
        return radarImageLoader
            .load(imageUrl = frame.imageUrl)
            .onResult(
                onError = { failure: Failure ->
                    displayPrecipitationError(failure = failure)
                    false
                },
                onSuccess = { bitmap: Bitmap ->
                    state.update { state: RadarState ->
                        state.copy(
                            precipitationIndex = index,
                            precipitationOverlay = bitmap
                        )
                    }
                    true
                }
            )
    }

    private suspend fun loadWindFrame(index: Int): Boolean {
        val forecast: WindForecast = windForecast.load() ?: return false
        val windFrame: WindFrame = forecast.frames.getOrNull(index = index) ?: return false
        val windField: WindField = windFieldRenderer.render(
            windGrid = forecast.grid,
            windFrame = windFrame,
            windPalette = windPalette.load()
        )
        state.update { state: RadarState ->
            state.copy(
                windIndex = index,
                windField = windField,
                isLoading = false
            )
        }
        return true
    }

    private fun onPlayButtonClicked() {
        if (state.value.isPlaying) {
            stopPlaying()
            return
        }
        state.update { state: RadarState ->
            state.copy(isPlaying = true)
        }
        frameJob.exchange(newValue = null)?.cancel()
        viewModelScope.launchReplacing(
            currentJob = playJob,
            context = coroutineDispatcher
        ) {
            play(layer = state.value.layer)
        }
    }

    private suspend fun CoroutineScope.play(layer: RadarLayer) {
        while (isActive) {
            delay(duration = layer.frameDuration())
            val radarState: RadarState = state.value
            val frameCount: Int = radarState.frameCount(layer = layer)
            if (frameCount == 0) {
                stopPlaying()
                return
            }
            val isShown: Boolean = loadFrame(
                layer = layer,
                index = (radarState.index(layer = layer) + 1) % frameCount
            )
            if (!isShown) {
                stopPlaying()
                return
            }
        }
    }

    private fun stopPlaying() {
        playJob.exchange(newValue = null)?.cancel()
        state.update { state: RadarState ->
            state.copy(isPlaying = false)
        }
    }

    private fun displayPrecipitationError(failure: Failure) {
        state.update { state: RadarState ->
            state.copy(isPrecipitationUnavailable = true)
        }
        displayError(failure = failure)
    }

    private fun displayError(failure: Failure) {
        state.update { state: RadarState ->
            state.copy(
                isLoading = false,
                errorMessageResourceId = failure.messageResourceId()
            )
        }
    }

    private fun RadarLayer.frameDuration(): Duration =
        when (this) {
            RadarLayer.PRECIPITATION -> precipitationFrameDuration
            RadarLayer.WIND -> windFrameDuration
        }

    private fun RadarState.frameCount(layer: RadarLayer): Int =
        when (layer) {
            RadarLayer.PRECIPITATION -> precipitationFrames.size
            RadarLayer.WIND -> windFrames.size
        }

    private fun RadarState.index(layer: RadarLayer): Int =
        when (layer) {
            RadarLayer.PRECIPITATION -> precipitationIndex
            RadarLayer.WIND -> windIndex
        }
}