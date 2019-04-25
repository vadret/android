package fi.kroon.vadret.presentation.radar

import fi.kroon.vadret.data.exception.Failure
import fi.kroon.vadret.data.functional.Either
import fi.kroon.vadret.domain.radar.GetRadarImageUrlService
import fi.kroon.vadret.presentation.shared.IViewModel
import fi.kroon.vadret.presentation.radar.di.RadarFeatureScope
import fi.kroon.vadret.utils.NIL_INT
import fi.kroon.vadret.utils.extensions.asObservable
import io.reactivex.Observable
import io.reactivex.ObservableTransformer
import timber.log.Timber
import javax.inject.Inject

@RadarFeatureScope
class RadarViewModel @Inject constructor(
    private var state: RadarView.State,
    private val getRadarImageUrlService: GetRadarImageUrlService
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

    private val eventToViewState = ObservableTransformer<RadarView.Event, RadarView.State> { upstream ->
        upstream.flatMap { event ->
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
        Timber.d("onSeekBarRestored")
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
        Timber.d("onSeekBarStopped")
        state = state.copy(
            renderEvent = RadarView.RenderEvent.None
        )
        return state.asObservable()
    }

    private fun restoreStateFromStateParcel(stateParcel: RadarView.StateParcel?) {
        Timber.d("restoreStateFromStateParcel: $stateParcel")
        stateParcel?.run {
            state = state.copy(
                isInitialised = isInitialised,
                isSeekBarRunning = false,
                wasRestoredFromStateParcel = true,
                currentSeekBarIndex = currentSeekBarIndex,
                seekBarMax = seekBarMax,
                timeStamp = timeStamp
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
        Timber.d("onPlayButtonStartedEvent")
        state = state.copy(
            isSeekBarRunning = true,
            renderEvent = RadarView.RenderEvent.StartSeekBar
        )
        return state.asObservable()
    }

    private fun onPlayButtonStoppedEvent(): Observable<RadarView.State> {
        Timber.d("onPlayButtonStoppedEvent")
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
            (state.isSeekBarRunning) -> {
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
        getRadarImageUrlService(state.timeStamp, state.currentSeekBarIndex)
            .flatMapObservable { result: Either<Failure, GetRadarImageUrlService.Data> ->
                result.either(
                    { failure: Failure ->
                        Timber.e("loadRadarImageUrl: $failure")
                        val errorCode: Int = getErrorCode(failure)
                        val renderEvent: RadarView.RenderEvent.DisplayError = RadarView.RenderEvent.DisplayError(errorCode)

                        state = state.copy(renderEvent = renderEvent, timeStamp = null)
                        state.asObservable()
                    },
                    { data: GetRadarImageUrlService.Data ->
                        val renderEvent: RadarView.RenderEvent.DisplayRadarImage = RadarView.RenderEvent.DisplayRadarImage(data.file!!)
                        state = state.copy(
                            renderEvent = renderEvent,
                            seekBarMax = data.maxIndex!!,
                            currentSeekBarIndex = state.currentSeekBarIndex,
                            timeStamp = data.timeStamp
                        )
                        state.asObservable()
                    }
                )
            }

    private fun onStateParcelUpdated(): Observable<RadarView.State> {
        Timber.d("onStateParcelUpdated")
        return radarPlayerHandler()
    }

    private fun onRadarImageDisplayed(newPosition: Int): Observable<RadarView.State> {
        Timber.d("onRadarImageDisplayed")
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