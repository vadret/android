package fi.kroon.vadret.presentation.radar

import android.os.SystemClock
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import fi.kroon.vadret.data.failure.Failure
import fi.kroon.vadret.domain.radar.GetRadarImageUrlService
import fi.kroon.vadret.domain.radar.GetRadarLastCheckedKeyValueTask
import fi.kroon.vadret.domain.radar.SetRadarLastCheckedKeyValueTask
import fi.kroon.vadret.presentation.shared.IViewModel
import fi.kroon.vadret.util.NIL_INT
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.rx2.await
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject
import kotlin.coroutines.coroutineContext

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
        is RadarView.Event.OnRadarImageDisplayed -> Unit //onRadarImageDisplayed(event.position)
        RadarView.Event.OnStateParcelUpdated -> Unit //onStateParcelUpdated()
        RadarView.Event.OnFailureHandled -> onFailureHandledEvent()
        RadarView.Event.OnPlayButtonClicked -> onPlayButtonClickedEvent()
        RadarView.Event.OnPlayButtonStarted -> Unit //onPlayButtonStartedEvent()
        RadarView.Event.OnPlayButtonStopped -> Unit //onPlayButtonStoppedEvent()
        RadarView.Event.OnSeekBarStopped -> onSeekBarStopped()
        RadarView.Event.OnSeekBarReset -> Unit //onSeekBarReset()
        RadarView.Event.OnSeekBarRestored -> onSeekBarRestored()
    }.also {
        Timber.d("EVENT: $event")
    }

    private suspend fun onViewInitialisedEvent(event: RadarView.Event.OnViewInitialised) {
        //restoreStateFromStateParcel(event.stateParcel)
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

    private suspend fun onPlayButtonClickedEvent() {

        val isRunning = stateModel.isSeekBarRunning
        val renderEvent = RadarView.RenderEvent.UpdatePlayerState(isRunning)
        stateModel = stateModel.copy(
            renderEvent = renderEvent,
            isSeekBarRunning = !isRunning
        )
        state.emit(stateModel)

    }

    private fun onPositionSeekEvent(position: Int) {
        // render event with DisplayRadarImage[position]
        stateModel = stateModel.copy(currentSeekBarIndex = position)
    }

    private suspend fun loadRadarImageUrl() = withContext(Dispatchers.IO) {
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

    private suspend fun onFailureHandledEvent() {
        stateModel = stateModel.copy(
            renderEvent = RadarView.RenderEvent.Idle,
            currentSeekBarIndex = NIL_INT,
            seekBarMax = NIL_INT,
            isSeekBarRunning = false
        )
        state.emit(stateModel)
    }
}