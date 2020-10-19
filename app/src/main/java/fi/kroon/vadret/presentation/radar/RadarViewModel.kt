package fi.kroon.vadret.presentation.radar

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import fi.kroon.vadret.data.failure.Failure
import fi.kroon.vadret.domain.radar.GetRadarImageUrlService
import fi.kroon.vadret.domain.radar.GetRadarLastCheckedKeyValueTask
import fi.kroon.vadret.domain.radar.SetRadarLastCheckedKeyValueTask
import fi.kroon.vadret.presentation.shared.IViewModel
import fi.kroon.vadret.util.NIL_INT
import fi.kroon.vadret.util.extension.asObservable
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.rx2.await
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject

@ExperimentalCoroutinesApi
class RadarViewModel @Inject constructor(
    private var stateModel: RadarView.State,
    private val state: MutableSharedFlow<RadarView.State>,
    private val getRadarImageUrlService: GetRadarImageUrlService,
    private val getRadarLastCheckedKeyValueTask: GetRadarLastCheckedKeyValueTask,
    private val setRadarLastCheckedKeyValueTask: SetRadarLastCheckedKeyValueTask
) : ViewModel(), IViewModel {

    val viewState: SharedFlow<RadarView.State> get() = state.asSharedFlow()

    fun send(event: RadarView.Event) {
        viewModelScope.launch { reduce(event = event) }
    }

    private suspend fun reduce(event: RadarView.Event) = when (event) {
        is RadarView.Event.OnViewInitialised -> onViewInitialisedEvent(event)
        is RadarView.Event.OnPositionUpdated -> onPositionSeekEvent(event.position)
        is RadarView.Event.OnRadarImageDisplayed -> onRadarImageDisplayed(event.position)
        RadarView.Event.OnStateParcelUpdated -> onStateParcelUpdated()
        RadarView.Event.OnFailureHandled -> onFailureHandledEvent()
        RadarView.Event.OnPlayButtonClicked -> onPlayButtonClickedEvent()
        RadarView.Event.OnPlayButtonStarted -> onPlayButtonStartedEvent()
        RadarView.Event.OnPlayButtonStopped -> onPlayButtonStoppedEvent()
        RadarView.Event.OnSeekBarStopped -> onSeekBarStopped()
        RadarView.Event.OnSeekBarReset -> Unit //onSeekBarReset()
        RadarView.Event.OnSeekBarRestored -> onSeekBarRestored()
    }

    private suspend fun onViewInitialisedEvent(event: RadarView.Event.OnViewInitialised) {
        restoreStateFromStateParcel(event.stateParcel)
        stateModel = stateModel.copy(isInitialised = true, renderEvent = RadarView.RenderEvent.Idle)
        state.emit(stateModel)
    }

    private suspend fun onSeekBarRestored() {
        Timber.d("ON SEEKBAR RESTORED")
        loadRadarImageUrl()
    }

    private suspend fun onSeekBarStopped() {
        Timber.d("ON SEEKBAR STOPPED")
        stateModel = stateModel.copy(
            renderEvent = RadarView.RenderEvent.Idle
        )
        state.emit(stateModel)
    }

    private fun restoreStateFromStateParcel(stateParcel: RadarView.StateParcel?) {
        Timber.d("RESTORE STATE FROM STATE PARCEL: $stateParcel")
        stateParcel?.run {
            stateModel = stateModel.copy(
                isInitialised = isInitialised,
                isSeekBarRunning = false,
                wasRestoredFromStateParcel = true,
                currentSeekBarIndex = currentSeekBarIndex,
                seekBarMax = seekBarMax
            )
        }
    }

    private suspend fun onPlayButtonClickedEvent() {
        handlePlayButtonClicks()
    }

    private suspend fun handlePlayButtonClicks() {
        when {
            stateModel.isSeekBarRunning -> {
                stateModel =
                    stateModel.copy(renderEvent = RadarView.RenderEvent.SetPlayButtonToStopped)
                state.emit(stateModel)
            }
            stateModel.isSeekBarRunning.not() -> {
                stateModel =
                    stateModel.copy(renderEvent = RadarView.RenderEvent.SetPlayButtonToPlaying)
                state.emit(stateModel)
            }
            else -> {
                stateModel = stateModel.copy(isSeekBarRunning = false)
                state.emit(stateModel)
            }
        }
    }

    private suspend fun onPlayButtonStartedEvent() {
        Timber.d("ON PLAY BUTTON STARTED EVENT")
        loadRadarImageUrl()
        // while running we emit a new state and increment while
//        while (stateModel.isSeekBarRunning ) {
//            stateModel = if (stateModel.currentSeekBarIndex < stateModel.seekBarMax) {
//                stateModel.copy(
//                    currentSeekBarIndex = stateModel.currentSeekBarIndex + 1,
//                    renderEvent = RadarView.RenderEvent.StartSeekBar(currentIndex = stateModel.currentSeekBarIndex)
//                )
//            } else {
//                stateModel.copy(
//                    currentSeekBarIndex = 1,
//                    renderEvent = RadarView.RenderEvent.StartSeekBar(currentIndex = stateModel.currentSeekBarIndex)
//                )
//            }
//            delay(300)
//            state.emit(stateModel)
//        }
    }

    private suspend fun onPlayButtonStoppedEvent() {
        Timber.d("ON PLAY BUTTON STOPPED EVENT")
        stateModel = stateModel.copy(
            isSeekBarRunning = false,
            renderEvent = RadarView.RenderEvent.StopSeekBar
        )
        state.emit(stateModel)
    }

    private fun onPositionSeekEvent(position: Int) {
        stateModel = stateModel.copy(currentSeekBarIndex = position)
    }

    private suspend fun loadRadarImageUrl() = withContext(Dispatchers.IO) {
        delay(300)
        getRadarLastCheckedKeyValueTask()
            .await()
            .either(
                { failure: Failure ->
                    Timber.e("failure: $failure")
                    stateModel = stateModel.copy(renderEvent = RadarView.RenderEvent.Idle)
                    state.emit(stateModel)
                },
                { timeStamp: Long ->
                    getRadarImageUrlService(timeStamp, stateModel.currentSeekBarIndex)
                        .await()
                        .either(
                            { failure: Failure ->
                                Timber.e("LOAD RADAR IMAGE URL: $failure")
                                val errorCode: Int = getErrorCode(failure)
                                val renderEvent: RadarView.RenderEvent.DisplayError =
                                    RadarView.RenderEvent.DisplayError(errorCode)
                                stateModel = stateModel.copy(renderEvent = renderEvent)
                                state.emit(stateModel)
                            },
                            { data: GetRadarImageUrlService.Data ->
                                val renderEvent: RadarView.RenderEvent.DisplayRadarImage =
                                    RadarView.RenderEvent.DisplayRadarImage(data.file!!)
                                stateModel = stateModel.copy(
                                    renderEvent = renderEvent,
                                    seekBarMax = data.maxIndex!!,
                                    currentSeekBarIndex = stateModel.currentSeekBarIndex
                                )
                                setLastCheckedTimeStamp(timeStamp = timeStamp)
                            }
                        )
                }
            )
    }

    private suspend fun setLastCheckedTimeStamp(timeStamp: Long) = withContext(Dispatchers.IO) {
        setRadarLastCheckedKeyValueTask(value = timeStamp)
            .await()
            .either(
                { failure: Failure ->
                    Timber.e("failure: $failure")
                    stateModel = stateModel.copy(renderEvent = RadarView.RenderEvent.Idle)
                    state.emit(stateModel)
                },
                {
                    Timber.d("LAST CHECKED UPDATED: $timeStamp")
                    state.emit(stateModel)
                }
            )
    }

    private suspend fun onStateParcelUpdated() {
        Timber.d("ON STATE PARCEL UPDATED")
        //radarPlayerHandler()
    }

    private suspend fun onRadarImageDisplayed(newPosition: Int) {
        Timber.d("ON RADAR IMAGE DISPLAYED")
        val currentSeekBarIndex: Int = if (newPosition < stateModel.seekBarMax) {
            newPosition + 1
        }
        else {
            NIL_INT
        }

        stateModel = stateModel.copy(currentSeekBarIndex = currentSeekBarIndex)

//        val renderEvent: RadarView.RenderEvent = when {
//            (newPosition == stateModel.seekBarMax && NIL_INT < stateModel.seekBarMax) -> {
//                stateModel = stateModel.copy(currentSeekBarIndex = NIL_INT)
//                RadarView.RenderEvent.ResetSeekBar
//            }
//            else -> {
//                RadarView.RenderEvent.UpdateStateParcel
//            }
//        }

        loadRadarImageUrl()
//        stateModel = stateModel.copy(
//            renderEvent = renderEvent,
//            currentSeekBarIndex = currentSeekBarIndex
//        )
//
//        state.emit(stateModel)
    }

    private suspend fun onFailureHandledEvent() {
        stateModel = stateModel.copy(
            renderEvent = RadarView.RenderEvent.SetPlayButtonToStopped,
            currentSeekBarIndex = NIL_INT,
            seekBarMax = NIL_INT,
            isSeekBarRunning = false
        )
        state.emit(stateModel)
    }
}