package fi.kroon.vadret.radar

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import fi.kroon.vadret.common.Failure
import fi.kroon.vadret.radar.extension.toRadarFrames
import fi.kroon.vadret.radar.model.Radar
import fi.kroon.vadret.radar.model.RadarFrame
import fi.kroon.vadret.ui.extension.messageResourceId
import io.github.sphrak.either.extension.onResult
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.datetime.TimeZone
import javax.inject.Inject
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds

/**
 *  How long each frame shows while playing.
 */
private val frameDuration: Duration = 256.milliseconds

/**
 *  Jobs are started and cancelled on the main thread only,
 *  the work itself runs on [coroutineDispatcher].
 */
class RadarViewModel @Inject internal constructor(
    private val getRadarTask: GetRadarTask,
    private val radarImageLoader: RadarImageLoader,
    private val coroutineDispatcher: CoroutineDispatcher
) : ViewModel() {

    private val mutableState: MutableStateFlow<RadarState> = MutableStateFlow(value = RadarState())
    val state: StateFlow<RadarState> = mutableState.asStateFlow()

    private var playJob: Job? = null
    private var frameJob: Job? = null

    init {
        load()
    }

    fun onEvent(event: RadarEvent) {
        when (event) {
            RadarEvent.OnPlayButtonClicked -> onPlayButtonClicked()
            RadarEvent.OnErrorShown -> mutableState.update { state: RadarState ->
                state.copy(errorMessageResourceId = null)
            }
            is RadarEvent.OnIndexChanged -> onIndexChanged(index = event.index)
        }
    }

    /**
     *  Shows the newest frame once loaded.
     */
    private fun load() {
        viewModelScope.launch {
            val isLoaded: Boolean = withContext(context = coroutineDispatcher) {
                getRadarTask()
                    .onResult(
                        onError = { failure: Failure ->
                            displayError(failure = failure)
                            false
                        },
                        onSuccess = { radar: Radar ->
                            displayFrames(frames = radar.toRadarFrames(timeZone = TimeZone.currentSystemDefault()))
                            true
                        }
                    )
            }
            if (isLoaded) {
                showFrame(index = mutableState.value.frames.lastIndex)
            }
        }
    }

    private fun displayFrames(frames: List<RadarFrame>) {
        mutableState.update { state: RadarState ->
            state.copy(
                frames = frames,
                index = frames.lastIndex.coerceAtLeast(minimumValue = 0),
                isLoading = false
            )
        }
    }

    /**
     *  The index moves with the slider right away, the image follows.
     */
    private fun onIndexChanged(index: Int) {
        stopPlaying()
        mutableState.update { state: RadarState ->
            state.copy(index = index)
        }
        showFrame(index = index)
    }

    /**
     *  A newer frame request replaces one still loading.
     */
    private fun showFrame(index: Int) {
        val frame: RadarFrame = mutableState.value.frames.getOrNull(index = index) ?: return
        frameJob?.cancel()
        frameJob = viewModelScope.launch {
            loadFrame(
                index = index,
                frame = frame
            )
        }
    }

    /**
     *  False when the image could not be loaded.
     */
    private suspend fun loadFrame(
        index: Int,
        frame: RadarFrame
    ): Boolean =
        withContext(context = coroutineDispatcher) {
            radarImageLoader
                .load(imageUrl = frame.imageUrl)
                .onResult(
                    onError = { failure: Failure ->
                        displayError(failure = failure)
                        false
                    },
                    onSuccess = { bitmap: Bitmap ->
                        mutableState.update { state: RadarState ->
                            state.copy(
                                index = index,
                                overlay = bitmap
                            )
                        }
                        true
                    }
                )
        }

    /**
     *  Plays from the shown frame and starts over after the newest.
     *  Each frame is shown before the next one is loaded, and a frame
     *  that fails to load stops playback.
     */
    private fun onPlayButtonClicked() {
        if (mutableState.value.isPlaying) {
            stopPlaying()
            return
        }
        mutableState.update { state: RadarState ->
            state.copy(isPlaying = true)
        }
        frameJob?.cancel()
        playJob = viewModelScope.launch {
            while (isActive) {
                delay(duration = frameDuration)
                val state: RadarState = mutableState.value
                if (state.frames.isEmpty()) {
                    stopPlaying()
                    return@launch
                }
                val index: Int = (state.index + 1) % state.frames.size
                val isShown: Boolean = loadFrame(
                    index = index,
                    frame = state.frames[index]
                )
                if (!isShown) {
                    stopPlaying()
                    return@launch
                }
            }
        }
    }

    private fun stopPlaying() {
        playJob?.cancel()
        playJob = null
        mutableState.update { state: RadarState ->
            state.copy(isPlaying = false)
        }
    }

    private fun displayError(failure: Failure) {
        mutableState.update { state: RadarState ->
            state.copy(
                isLoading = false,
                errorMessageResourceId = failure.messageResourceId()
            )
        }
    }
}