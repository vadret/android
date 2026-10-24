package fi.kroon.vadret.radar

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import fi.kroon.vadret.common.Failure
import fi.kroon.vadret.radar.extension.toRadarFrames
import fi.kroon.vadret.radar.model.Radar
import fi.kroon.vadret.radar.model.RadarFrame
import fi.kroon.vadret.ui.extension.launchReplacing
import fi.kroon.vadret.ui.extension.messageResourceId
import io.github.sphrak.either.extension.onResult
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.datetime.TimeZone
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicReference
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds

private val frameDuration: Duration = 256.milliseconds

class RadarViewModel internal constructor(
    private val getRadarTask: GetRadarTask,
    private val radarImageLoader: RadarImageLoader,
    private val coroutineDispatcher: CoroutineDispatcher
) : ViewModel() {

    val state: StateFlow<RadarState>
        field = MutableStateFlow(value = RadarState())

    private val isStarted: AtomicBoolean = AtomicBoolean(false)
    private val playJob: AtomicReference<Job?> = AtomicReference(null)
    private val frameJob: AtomicReference<Job?> = AtomicReference(null)

    fun onEvent(event: RadarEvent) {
        when (event) {
            RadarEvent.OnStart -> onStart()
            RadarEvent.OnPlayButtonClicked -> onPlayButtonClicked()
            RadarEvent.OnErrorShown -> state.update { state: RadarState ->
                state.copy(errorMessageResourceId = null)
            }
            is RadarEvent.OnIndexChanged -> onIndexChanged(index = event.index)
        }
    }

    private fun onStart() {
        if (isStarted.compareAndSet(false, true)) {
            load()
        }
    }

    private fun load() {
        viewModelScope.launch(context = coroutineDispatcher) {
            getRadarTask()
                .onResult(
                    onError = { failure: Failure -> displayError(failure = failure) },
                    onSuccess = { radar: Radar ->
                        val frames: ImmutableList<RadarFrame> = radar
                            .toRadarFrames(timeZone = TimeZone.currentSystemDefault())
                            .toImmutableList()
                        displayFrames(frames = frames)
                        showFrame(index = frames.lastIndex)
                    }
                )
        }
    }

    private fun displayFrames(frames: ImmutableList<RadarFrame>) {
        state.update { state: RadarState ->
            state.copy(
                frames = frames,
                index = frames.lastIndex.coerceAtLeast(minimumValue = 0),
                isLoading = false
            )
        }
    }

    private fun onIndexChanged(index: Int) {
        stopPlaying()
        state.update { state: RadarState ->
            state.copy(index = index)
        }
        showFrame(index = index)
    }

    private fun showFrame(index: Int) {
        val frame: RadarFrame = state.value.frames.getOrNull(index = index) ?: return
        viewModelScope.launchReplacing(
            currentJob = frameJob,
            context = coroutineDispatcher
        ) {
            loadFrame(
                index = index,
                frame = frame
            )
        }
    }

    private suspend fun loadFrame(
        index: Int,
        frame: RadarFrame
    ): Boolean =
        radarImageLoader
            .load(imageUrl = frame.imageUrl)
            .onResult(
                onError = { failure: Failure ->
                    displayError(failure = failure)
                    false
                },
                onSuccess = { bitmap: Bitmap ->
                    state.update { state: RadarState ->
                        state.copy(
                            index = index,
                            overlay = bitmap
                        )
                    }
                    true
                }
            )

    private fun onPlayButtonClicked() {
        if (state.value.isPlaying) {
            stopPlaying()
            return
        }
        state.update { state: RadarState ->
            state.copy(isPlaying = true)
        }
        frameJob.getAndSet(null)?.cancel()
        viewModelScope.launchReplacing(
            currentJob = playJob,
            context = coroutineDispatcher
        ) {
            play()
        }
    }

    private suspend fun CoroutineScope.play() {
        while (isActive) {
            delay(duration = frameDuration)
            val radarState: RadarState = state.value
            if (radarState.frames.isEmpty()) {
                stopPlaying()
                return
            }
            val index: Int = (radarState.index + 1) % radarState.frames.size
            val isShown: Boolean = loadFrame(
                index = index,
                frame = radarState.frames[index]
            )
            if (!isShown) {
                stopPlaying()
                return
            }
        }
    }

    private fun stopPlaying() {
        playJob.getAndSet(null)?.cancel()
        state.update { state: RadarState ->
            state.copy(isPlaying = false)
        }
    }

    private fun displayError(failure: Failure) {
        state.update { state: RadarState ->
            state.copy(
                isLoading = false,
                errorMessageResourceId = failure.messageResourceId()
            )
        }
    }
}
