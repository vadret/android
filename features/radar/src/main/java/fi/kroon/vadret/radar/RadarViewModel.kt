package fi.kroon.vadret.radar

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import fi.kroon.vadret.data.failure.Failure
import fi.kroon.vadret.data.radar.model.Radar
import fi.kroon.vadret.radar.model.RadarFrame
import fi.kroon.vadret.shared.IViewModel
import fi.kroon.vadret.util.RADAR_DEBOUNCE_MILLIS
import io.github.sphrak.either.extension.onResult
import io.github.sphrak.either.flatMap
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.TimeZone
import timber.log.Timber
import javax.inject.Inject

class RadarViewModel @Inject constructor(
    private val getRadarImageUrlService: GetRadarImageUrlService,
    private val getRadarLastCheckedKeyValueTask: GetRadarLastCheckedKeyValueTask,
    private val setRadarLastCheckedKeyValueTask: SetRadarLastCheckedKeyValueTask,
    private val radarImageLoader: RadarImageLoader,
    private val coroutineDispatcher: CoroutineDispatcher
) : ViewModel(), IViewModel {

    private val mutableState: MutableStateFlow<RadarState> = MutableStateFlow(RadarState())
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

    private fun load() {
        viewModelScope.launch(context = coroutineDispatcher) {
            getRadarLastCheckedKeyValueTask()
                .flatMap { timeStamp: Long -> getRadarImageUrlService(timeStamp = timeStamp) }
                .onResult(
                    { failure: Failure ->
                        displayError(failure = failure)
                    },
                    { data: GetRadarImageUrlService.Data ->
                        val radar: Radar? = data.radar
                        val frames: List<RadarFrame> = if (radar != null) {
                            RadarFrameMapper(
                                files = radar.files,
                                timeZone = TimeZone.currentSystemDefault()
                            )
                        } else {
                            emptyList()
                        }
                        mutableState.update { state: RadarState ->
                            state.copy(
                                frames = frames,
                                index = frames.lastIndex.coerceAtLeast(minimumValue = 0),
                                isLoading = false
                            )
                        }
                        setLastChecked(timeStamp = data.timeStamp)
                        showFrame(index = frames.lastIndex)
                    }
                )
        }
    }

    /**
     *  A lost timestamp only means the next load skips the cache.
     */
    private suspend fun setLastChecked(timeStamp: Long) {
        setRadarLastCheckedKeyValueTask(value = timeStamp)
            .onResult(
                { failure: Failure ->
                    Timber.e("Failed storing last checked: $failure")
                },
                {}
            )
    }

    private suspend fun showFrame(index: Int) {
        val frame: RadarFrame = mutableState.value.frames.getOrNull(index) ?: return
        radarImageLoader.load(url = frame.imageUrl)
            .onResult(
                { failure: Failure ->
                    displayError(failure = failure)
                },
                { bitmap: Bitmap ->
                    mutableState.update { state: RadarState ->
                        state.copy(
                            index = index,
                            overlay = bitmap
                        )
                    }
                }
            )
    }

    /**
     *  Plays from the shown frame and starts over after the newest.
     */
    private fun onPlayButtonClicked() {
        if (mutableState.value.isPlaying) {
            stopPlaying()
            return
        }
        mutableState.update { state: RadarState ->
            state.copy(isPlaying = true)
        }
        playJob = viewModelScope.launch(context = coroutineDispatcher) {
            while (true) {
                delay(timeMillis = RADAR_DEBOUNCE_MILLIS)
                val state: RadarState = mutableState.value
                if (state.frames.isEmpty()) {
                    stopPlaying()
                    return@launch
                }
                showFrame(index = (state.index + 1) % state.frames.size)
            }
        }
    }

    private fun stopPlaying() {
        playJob?.cancel()
        mutableState.update { state: RadarState ->
            state.copy(isPlaying = false)
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
        frameJob?.cancel()
        frameJob = viewModelScope.launch(context = coroutineDispatcher) {
            showFrame(index = index)
        }
    }

    private fun displayError(failure: Failure) {
        Timber.e("Failure: $failure")
        playJob?.cancel()
        mutableState.update { state: RadarState ->
            state.copy(
                isLoading = false,
                isPlaying = false,
                errorMessageResourceId = getErrorCode(failure)
            )
        }
    }
}