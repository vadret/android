package fi.kroon.vadret.presentation.radar

import fi.kroon.vadret.data.failure.Failure
import fi.kroon.vadret.domain.radar.GetRadarImageUrlService
import fi.kroon.vadret.domain.radar.GetRadarLastCheckedKeyValueTask
import fi.kroon.vadret.domain.radar.SetRadarLastCheckedKeyValueTask
import fi.kroon.vadret.presentation.radar.di.RadarScope
import fi.kroon.vadret.presentation.shared.IViewModel
import fi.kroon.vadret.util.NIL_INT
import fi.kroon.vadret.util.extension.asObservable
import io.github.sphrak.either.Either
import io.reactivex.Observable
import io.reactivex.ObservableTransformer
import timber.log.Timber
import javax.inject.Inject

@RadarScope
class RadarViewModel @Inject constructor(
    private var state: RadarView.State,
    private val getRadarImageUrlService: GetRadarImageUrlService,
    private val getRadarLastCheckedKeyValueTask: GetRadarLastCheckedKeyValueTask,
    private val setRadarLastCheckedKeyValueTask: SetRadarLastCheckedKeyValueTask
) : IViewModel {

    operator fun invoke(): ObservableTransformer<RadarView.Event, RadarView.State> = onEvent

    private val onEvent = ObservableTransformer<RadarView.Event, RadarView.State> { upstream: Observable<RadarView.Event> ->
        upstream.publish { shared: Observable<RadarView.Event> ->
            Observable.mergeArray(
                shared.ofType(RadarView.Event.OnViewInitialised::class.java),
                shared.ofType(RadarView.Event.OnFailureHandled::class.java),
                shared.ofType(RadarView.Event.OnRadarImageDisplayed::class.java),
                shared.ofType(RadarView.Event.OnStateParcelUpdated::class.java),
                shared.ofType(RadarView.Event.OnSeekBarRestored::class.java),
                shared.ofType(RadarView.Event.OnSeekBarStopped::class.java),
                shared.ofType(RadarView.Event.OnPlayButtonStarted::class.java),
                shared.ofType(RadarView.Event.OnPlayButtonStopped::class.java),
                shared.ofType(RadarView.Event.OnSeekBarReset::class.java),
                shared.ofType(RadarView.Event.OnPlayButtonClicked::class.java),
                shared.ofType(RadarView.Event.OnPositionUpdated::class.java)
            ).compose(
                eventToViewState
            )
        }
    }

    private val eventToViewState = ObservableTransformer<RadarView.Event, RadarView.State> { upstream: Observable<RadarView.Event> ->
        upstream.flatMap { event: RadarView.Event ->
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
        }
    }

    private fun onViewInitialisedEvent(event: RadarView.Event.OnViewInitialised): Observable<RadarView.State> {
        restoreStateFromStateParcel(event.stateParcel)
        state = state.copy(isInitialised = true, renderEvent = RadarView.RenderEvent.None)
        return restoreSeekBarIfNeeded()
    }

    private fun onSeekBarRestored(): Observable<RadarView.State> {
        Timber.d("ON SEEKBAR RESTORED")
        return loadRadarImageUrl()
    }

    private fun restoreSeekBarIfNeeded(): Observable<RadarView.State> =
        if (state.wasRestoredFromStateParcel) {
            state = state.copy(
                renderEvent = RadarView.RenderEvent.RestoreSeekBarPosition,
                wasRestoredFromStateParcel = false
            )
            state.asObservable()
        } else {
            loadRadarImageUrl()
        }

    private fun onSeekBarStopped(): Observable<RadarView.State> {
        Timber.d("ON SEEKBAR STOPPED")
        state = state.copy(
            renderEvent = RadarView.RenderEvent.None
        )
        return state.asObservable()
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

    private fun onPlayButtonClickedEvent(): Observable<RadarView.State> =
        handlePlayButtonClicks()

    private fun handlePlayButtonClicks(): Observable<RadarView.State> =
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
        }.asObservable()

    private fun onPlayButtonStartedEvent(): Observable<RadarView.State> {
        Timber.d("ON PLAY BUTTON STARTED EVENT")
        state = state.copy(
            isSeekBarRunning = true,
            renderEvent = RadarView.RenderEvent.StartSeekBar
        )
        return state.asObservable()
    }

    private fun onPlayButtonStoppedEvent(): Observable<RadarView.State> {
        Timber.d("ON PLAY BUTTON STOPPED EVENT")
        state = state.copy(
            isSeekBarRunning = false,
            renderEvent = RadarView.RenderEvent.StopSeekBar
        )
        return state.asObservable()
    }

    private fun onPositionSeekedEvent(position: Int): Observable<RadarView.State> {
        state = state.copy(currentSeekBarIndex = position)
        return radarPlayerHandler()
    }

    private fun onSeekBarReset(): Observable<RadarView.State> =
        radarPlayerHandler()

    private fun radarPlayerHandler(): Observable<RadarView.State> =
        when {
            state.isSeekBarRunning -> {
                loadRadarImageUrl()
            }
            else -> {
                state = state.copy(
                    renderEvent = RadarView.RenderEvent.None
                )
                state.asObservable()
            }
        }

    private fun loadRadarImageUrl(): Observable<RadarView.State> =
        getRadarLastCheckedKeyValueTask()
            .flatMapObservable { result: Either<Failure, Long> ->
                result.either(
                    { failure: Failure ->
                        Timber.e("failure: $failure")
                        state = state.copy(renderEvent = RadarView.RenderEvent.None)
                        state.asObservable()
                    },
                    { timeStamp: Long ->
                        getRadarImageUrlService(timeStamp, state.currentSeekBarIndex)
                            .flatMapObservable { result: Either<Failure, GetRadarImageUrlService.Data> ->
                                result.either(
                                    { failure: Failure ->
                                        Timber.e("LOAD RADAR IMAGE URL: $failure")
                                        val errorCode: Int = getErrorCode(failure)
                                        val renderEvent: RadarView.RenderEvent.DisplayError = RadarView.RenderEvent.DisplayError(errorCode)

                                        state = state.copy(renderEvent = renderEvent)
                                        state.asObservable()
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
                    }
                )
            }

    private fun setLastCheckedTimeStamp(timeStamp: Long): Observable<RadarView.State> =
        setRadarLastCheckedKeyValueTask(value = timeStamp)
            .flatMapObservable { result: Either<Failure, Unit> ->
                result.either(
                    { failure: Failure ->
                        Timber.e("failure: $failure")
                        state = state.copy(renderEvent = RadarView.RenderEvent.None)
                        state.asObservable()
                    },
                    {
                        Timber.d("LAST CHECKED UPDATED: $timeStamp")
                        state.asObservable()
                    }
                )
            }

    private fun onStateParcelUpdated(): Observable<RadarView.State> {
        Timber.d("ON STATE PARCEL UPDATED")
        return radarPlayerHandler()
    }

    private fun onRadarImageDisplayed(newPosition: Int): Observable<RadarView.State> {
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

        return state.asObservable()
    }

    private fun onFailureHandledEvent(): Observable<RadarView.State> {
        state = state.copy(
            renderEvent = RadarView.RenderEvent.SetPlayButtonToStopped,
            currentSeekBarIndex = NIL_INT,
            seekBarMax = NIL_INT,
            isSeekBarRunning = false
        )
        return state.asObservable()
    }
}