package fi.kroon.vadret.presentation.alert

import fi.kroon.vadret.data.exception.Failure
import fi.kroon.vadret.data.functional.Either
import fi.kroon.vadret.domain.alert.GetAlertService
import fi.kroon.vadret.presentation.BaseViewModel
import fi.kroon.vadret.presentation.alert.di.AlertFeatureScope
import fi.kroon.vadret.utils.extensions.asObservable
import io.reactivex.Observable
import io.reactivex.ObservableTransformer
import timber.log.Timber
import javax.inject.Inject

@AlertFeatureScope
class AlertViewModel @Inject constructor(
    private var state: AlertView.State,
    private val getAlertService: GetAlertService
) : BaseViewModel() {

    operator fun invoke(): ObservableTransformer<AlertView.Event, AlertView.State> = onEvent

    private val onEvent = ObservableTransformer<AlertView.Event,
        AlertView.State> { upstream: Observable<AlertView.Event> ->
        upstream.publish { shared: Observable<AlertView.Event> ->
            Observable.mergeArray(
                shared.ofType(AlertView.Event.OnFailureHandled::class.java),
                shared.ofType(AlertView.Event.OnViewInitialised::class.java),
                shared.ofType(AlertView.Event.OnProgressBarEffectStarted::class.java),
                shared.ofType(AlertView.Event.OnProgressBarEffectStopped::class.java),
                shared.ofType(AlertView.Event.OnSwipedToRefresh::class.java),
                shared.ofType(AlertView.Event.OnAlertListDisplayed::class.java),
                shared.ofType(AlertView.Event.OnScrollPositionRestored::class.java),
                shared.ofType(AlertView.Event.OnStateParcelUpdated::class.java)
            ).compose(
                eventToViewState
            )
        }
    }

    private val eventToViewState = ObservableTransformer<AlertView.Event, AlertView.State> { upstream ->
        upstream.flatMap { event ->
            when (event) {
                is AlertView.Event.OnViewInitialised -> onViewInitialisedEvent(event)
                AlertView.Event.OnFailureHandled -> onFailureHandledEvent()
                AlertView.Event.OnProgressBarEffectStarted -> onProgressBarEffectStartedEvent()
                AlertView.Event.OnProgressBarEffectStopped -> onProgressBarEffectStoppedEvent()
                AlertView.Event.OnSwipedToRefresh -> onSwipedToRefreshEvent()
                AlertView.Event.OnAlertListDisplayed -> onAlertListDisplayedEvent()
                AlertView.Event.OnScrollPositionRestored -> onScrollPositionRestoredEvent()
                AlertView.Event.OnStateParcelUpdated -> onStateParcelUpdatedEvent()
            }
        }
    }

    private fun preLoadingAlert(): Observable<AlertView.State> =
        when {
            state.startRefreshing -> {
                Timber.d("AlertView.RenderEvent.StartProgressBarEffect: state.startRefreshing = ${state.startRefreshing}")
                state = state.copy(
                    renderEvent = AlertView.RenderEvent.StartProgressBarEffect,
                    startRefreshing = false,
                    stopRefreshing = true
                )
                state.asObservable()
            }
            else -> {
                Timber.d("preLoadingAlert ended")
                state = state.copy(
                    isInitialised = true,
                    timeStamp = currentTimeMillis
                )
                loadAlert()
            }
        }

    private fun endLoadingAlert(): Observable<AlertView.State> =
        when {
            state.wasRestoredFromStateParcel -> {
                Timber.d("AlertView.RenderEvent.RestoreScrollPosition: state.wasRestoredFromStateParcel = ${state.wasRestoredFromStateParcel}")
                state = state.copy(
                    renderEvent = AlertView.RenderEvent.RestoreScrollPosition,
                    wasRestoredFromStateParcel = false
                )
                state.asObservable()
            }
            state.stopRefreshing -> {
                Timber.d("AlertView.RenderEvent.StopProgressBarEffect: state.stopRefreshing = ${state.stopRefreshing}")
                state = state.copy(
                    renderEvent = AlertView.RenderEvent.StopProgressBarEffect,
                    stopRefreshing = false
                )
                state.asObservable()
            }
            else -> {
                Timber.d("endLoadingAlert ended.")
                updateStateParcel()
            }
        }

    private fun restoreStateFromStateParcel(stateParcel: AlertView.StateParcel?) {
        Timber.d("restoreStateFromStateParcel: $stateParcel")
        stateParcel?.run {
            state = state.copy(
                forceNet = forceNet,
                wasRestoredFromStateParcel = true,
                startRefreshing = startRefreshing,
                stopRefreshing = stopRefreshing,
                timeStamp = timeStamp
            )
        }
    }

    private fun updateStateParcel(): Observable<AlertView.State> {
        Timber.d("updateStateParcel")
        state = state.copy(
            renderEvent = AlertView.RenderEvent.UpdateStateParcel,
            startRefreshing = false,
            stopRefreshing = false,
            forceNet = false
        )
        return state.asObservable()
    }

    private fun updateStateToLoadingAndRefreshing(): Observable<AlertView.State> {
        Timber.d("updateStateToLoadingAndRefreshing")
        state = state.copy(
            startRefreshing = state.wasRestoredFromStateParcel.not()
        )
        return state.asObservable()
    }

    private fun updateStateToRefreshing(): Observable<AlertView.State> {
        Timber.d("updateStateToRefreshing")
        state = state.copy(
            startRefreshing = true
        )
        return state.asObservable()
    }

    private fun onViewInitialisedEvent(event: AlertView.Event.OnViewInitialised): Observable<AlertView.State> {
        restoreStateFromStateParcel(event.stateParcel)
        updateStateToLoadingAndRefreshing()
        return preLoadingAlert()
    }

    private fun loadAlert(): Observable<AlertView.State> =
        getAlertService(state.timeStamp, state.forceNet)
            .map { result: Either<Failure, GetAlertService.Data> ->
                result.either(
                    { failure: Failure ->
                        Timber.e("loadAlertFailure: $failure")
                        val errorCode: Int = getErrorCode(failure)
                        val renderEvent: AlertView.RenderEvent.DisplayError = AlertView.RenderEvent.DisplayError(errorCode)

                        state = state.copy(renderEvent = renderEvent, timeStamp = null)
                        state
                    },
                    { data: GetAlertService.Data ->
                        val renderEvent: AlertView.RenderEvent.DisplayAlert =
                            AlertView.RenderEvent.DisplayAlert(
                                data.baseWarningItemModelList
                            )
                        state = state.copy(renderEvent = renderEvent, timeStamp = data.timeStamp)
                        state
                    }
                )
            }.toObservable()

    private fun onFailureHandledEvent(): Observable<AlertView.State> {
        Timber.d("Failure handled?")
        return endLoadingAlert()
    }

    private fun onProgressBarEffectStartedEvent(): Observable<AlertView.State> {
        return preLoadingAlert()
    }

    private fun onProgressBarEffectStoppedEvent(): Observable<AlertView.State> {
        return endLoadingAlert()
    }

    private fun onSwipedToRefreshEvent(): Observable<AlertView.State> {
        Timber.d("SWIPED ALERT REFRESH")
        state = state.copy(forceNet = true)
        updateStateToRefreshing()
        return preLoadingAlert()
    }

    private fun onAlertListDisplayedEvent(): Observable<AlertView.State> {
        return endLoadingAlert()
    }

    private fun onScrollPositionRestoredEvent(): Observable<AlertView.State> {
        return endLoadingAlert()
    }

    private fun onStateParcelUpdatedEvent(): Observable<AlertView.State> {
        state = state.copy(
            renderEvent = AlertView.RenderEvent.None
        )
        return state.asObservable()
    }
}