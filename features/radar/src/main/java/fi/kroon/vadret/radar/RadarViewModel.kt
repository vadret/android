package fi.kroon.vadret.radar

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import fi.kroon.vadret.data.failure.Failure
import fi.kroon.vadret.radar.di.RadarScope
import fi.kroon.vadret.shared.IViewModel
import fi.kroon.vadret.util.NIL_INT
import io.github.sphrak.either.extension.onResult
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@RadarScope
class RadarViewModel @Inject constructor(
    private var state: RadarView.State,
    private val getRadarImageUrlService: GetRadarImageUrlService,
    private val getRadarLastCheckedKeyValueTask: GetRadarLastCheckedKeyValueTask,
    private val setRadarLastCheckedKeyValueTask: SetRadarLastCheckedKeyValueTask
) : ViewModel(), IViewModel {

    private val mutableViewState: MutableSharedFlow<RadarView.State> = MutableSharedFlow()

    val viewState: SharedFlow<RadarView.State> get() = mutableViewState.asSharedFlow()

    fun send(event: RadarView.Event) {
        viewModelScope.launch { reduce(event = event) }
    }

    private suspend fun reduce(event: RadarView.Event): Unit =
        when (event) {
            is RadarView.Event.OnViewInitialised -> onViewInitialisedEvent(event)
            is RadarView.Event.OnPositionUpdated -> onPositionSeekedEvent(event.position)
            is RadarView.Event.OnRadarImageDisplayed -> onRadarImageDisplayed(event.position)
            RadarView.Event.OnStateParcelUpdated -> onStateParcelUpdated()
            RadarView.Event.OnFailureHandled -> onFailureHandledEvent()
            RadarView.Event.OnPlayButtonClicked -> onPlayButtonClickedEvent()
            RadarView.Event.OnPlayButtonStarted -> onPlayButtonStartedEvent()
            RadarView.Event.OnPlayButtonStopped -> onPlayButtonStoppedEvent()
            RadarView.Event.OnSeekBarStopped -> onSeekBarStopped()
            RadarView.Event.OnSeekBarReset -> onSeekBarReset()
            RadarView.Event.OnSeekBarRestored -> onSeekBarRestored()
        }

    private suspend fun emitState(): Unit =
        mutableViewState.emit(state)

    private suspend fun onViewInitialisedEvent(event: RadarView.Event.OnViewInitialised) {
        restoreStateFromStateParcel(event.stateParcel)
        state = state.copy(isInitialised = true, renderEvent = RadarView.RenderEvent.None)
        restoreSeekBarIfNeeded()
    }

    private suspend fun onSeekBarRestored() {
        Timber.d("ON SEEKBAR RESTORED")
        loadRadarImageUrl()
    }

    private suspend fun restoreSeekBarIfNeeded(): Unit =
        if (state.wasRestoredFromStateParcel) {
            state = state.copy(
                renderEvent = RadarView.RenderEvent.RestoreSeekBarPosition,
                wasRestoredFromStateParcel = false
            )
            emitState()
        } else {
            loadRadarImageUrl()
        }

    private suspend fun onSeekBarStopped() {
        Timber.d("ON SEEKBAR STOPPED")
        state = state.copy(
            renderEvent = RadarView.RenderEvent.None
        )
        emitState()
    }

    private fun restoreStateFromStateParcel(stateParcel: RadarView.StateParcel?) {
        Timber.d("RESTORE STATE FROM STATE PARCEL: $stateParcel")
        stateParcel?.run {
            state = state.copy(
                isInitialised = isInitialised,
                isSeekBarRunning = false,
                wasRestoredFromStateParcel = true,
                currentSeekBarIndex = currentSeekBarIndex,
                seekBarMax = seekBarMax
            )
        }
    }

    private suspend fun onPlayButtonClickedEvent(): Unit =
        handlePlayButtonClicks()

    private suspend fun handlePlayButtonClicks(): Unit =
        when {
            state.isSeekBarRunning -> {
                state = state.copy(renderEvent = RadarView.RenderEvent.SetPlayButtonToStopped)
                state
            }
            state.isSeekBarRunning.not() -> {
                state = state.copy(renderEvent = RadarView.RenderEvent.SetPlayButtonToPlaying)
                state
            }
            else -> {
                state = state.copy(isSeekBarRunning = false)
                state
            }
        }.let { emitState() }

    private suspend fun onPlayButtonStartedEvent() {
        Timber.d("ON PLAY BUTTON STARTED EVENT")
        state = state.copy(
            isSeekBarRunning = true,
            renderEvent = RadarView.RenderEvent.StartSeekBar
        )
        emitState()
    }

    private suspend fun onPlayButtonStoppedEvent() {
        Timber.d("ON PLAY BUTTON STOPPED EVENT")
        state = state.copy(
            isSeekBarRunning = false,
            renderEvent = RadarView.RenderEvent.StopSeekBar
        )
        emitState()
    }

    private suspend fun onPositionSeekedEvent(position: Int) {
        state = state.copy(currentSeekBarIndex = position)
        radarPlayerHandler()
    }

    private suspend fun onSeekBarReset(): Unit =
        radarPlayerHandler()

    private suspend fun radarPlayerHandler(): Unit =
        when {
            state.isSeekBarRunning -> {
                loadRadarImageUrl()
            }
            else -> {
                state = state.copy(
                    renderEvent = RadarView.RenderEvent.None
                )
                emitState()
            }
        }

    private suspend fun loadRadarImageUrl(): Unit =
        getRadarLastCheckedKeyValueTask()
            .onResult(
                { failure: Failure ->
                    Timber.e("failure: $failure")
                    state = state.copy(renderEvent = RadarView.RenderEvent.None)
                    emitState()
                },
                { timeStamp: Long ->
                    getRadarImageUrlService(timeStamp, state.currentSeekBarIndex)
                        .onResult(
                            { failure: Failure ->
                                Timber.e("LOAD RADAR IMAGE URL: $failure")
                                val errorCode: Int = getErrorCode(failure)
                                val renderEvent: RadarView.RenderEvent.DisplayError = RadarView.RenderEvent.DisplayError(errorCode)

                                state = state.copy(renderEvent = renderEvent)
                                emitState()
                            },
                            { data: GetRadarImageUrlService.Data ->
                                val renderEvent: RadarView.RenderEvent.DisplayRadarImage = RadarView.RenderEvent.DisplayRadarImage(data.file!!)
                                state = state.copy(
                                    renderEvent = renderEvent,
                                    seekBarMax = data.maxIndex!!,
                                    currentSeekBarIndex = state.currentSeekBarIndex
                                )
                                setLastCheckedTimeStamp(timeStamp = timeStamp)
                            }
                        )
                }
            )

    private suspend fun setLastCheckedTimeStamp(timeStamp: Long): Unit =
        setRadarLastCheckedKeyValueTask(value = timeStamp)
            .onResult(
                { failure: Failure ->
                    Timber.e("failure: $failure")
                    state = state.copy(renderEvent = RadarView.RenderEvent.None)
                    emitState()
                },
                {
                    Timber.d("LAST CHECKED UPDATED: $timeStamp")
                    emitState()
                }
            )

    private suspend fun onStateParcelUpdated() {
        Timber.d("ON STATE PARCEL UPDATED")
        radarPlayerHandler()
    }

    private suspend fun onRadarImageDisplayed(newPosition: Int) {
        Timber.d("ON RADAR IMAGE DISPLAYED")
        val currentSeekBarIndex: Int = when {
            (newPosition < state.seekBarMax) -> {
                newPosition
            }
            else -> {
                NIL_INT
            }
        }

        val renderEvent: RadarView.RenderEvent = when {
            (newPosition == state.seekBarMax && NIL_INT < state.seekBarMax) -> {
                state = state.copy(currentSeekBarIndex = NIL_INT)
                RadarView.RenderEvent.ResetSeekBar
            }
            else -> {
                RadarView.RenderEvent.UpdateStateParcel
            }
        }

        state = state.copy(
            renderEvent = renderEvent,
            currentSeekBarIndex = currentSeekBarIndex
        )

        emitState()
    }

    private suspend fun onFailureHandledEvent() {
        state = state.copy(
            renderEvent = RadarView.RenderEvent.SetPlayButtonToStopped,
            currentSeekBarIndex = NIL_INT,
            seekBarMax = NIL_INT,
            isSeekBarRunning = false
        )
        emitState()
    }
}