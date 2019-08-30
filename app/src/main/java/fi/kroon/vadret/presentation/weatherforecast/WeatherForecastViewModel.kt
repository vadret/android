package fi.kroon.vadret.presentation.weatherforecast

import fi.kroon.vadret.data.exception.Failure
import fi.kroon.vadret.data.nominatim.model.Locality
import fi.kroon.vadret.domain.weatherforecast.GetAppLocationModeTask
import fi.kroon.vadret.domain.weatherforecast.GetAutoCompleteService
import fi.kroon.vadret.domain.weatherforecast.GetWeatherForecastLastCheckedTask
import fi.kroon.vadret.domain.weatherforecast.GetWeatherForecastService
import fi.kroon.vadret.domain.weatherforecast.SetDefaultLocationInformationTask
import fi.kroon.vadret.domain.weatherforecast.SetLocationInformationTask
import fi.kroon.vadret.domain.weatherforecast.SetLocationModeTask
import fi.kroon.vadret.domain.weatherforecast.SetWeatherForecastLastCheckedTask
import fi.kroon.vadret.presentation.shared.IViewModel
import fi.kroon.vadret.presentation.weatherforecast.di.WeatherForecastFeatureScope
import fi.kroon.vadret.util.AUTOCOMPLETE_DEBOUNCE_MILLIS
import fi.kroon.vadret.util.extension.asObservable
import fi.kroon.vadret.util.extension.empty
import io.github.sphrak.either.Either
import io.reactivex.Observable
import io.reactivex.ObservableTransformer
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import timber.log.Timber

@WeatherForecastFeatureScope
class WeatherForecastViewModel @Inject constructor(
    private var state: WeatherForecastView.State,
    private val setDefaultLocationInformationTask: SetDefaultLocationInformationTask,
    private val getWeatherForecastService: GetWeatherForecastService,
    private val getAutoCompleteService: GetAutoCompleteService,
    private val setLocationInformationTask: SetLocationInformationTask,
    private val enableGpsLocationModeTask: SetLocationModeTask,
    private val getAppLocationModeTask: GetAppLocationModeTask,
    private val setWeatherForecastLastCheckedTask: SetWeatherForecastLastCheckedTask,
    private val getWeatherForecastLastCheckedTask: GetWeatherForecastLastCheckedTask
) : IViewModel {

    operator fun invoke(): ObservableTransformer<WeatherForecastView.Event,
        WeatherForecastView.State> = onEvent

    private val onEvent = ObservableTransformer<WeatherForecastView.Event,
        WeatherForecastView.State> { upstream: Observable<WeatherForecastView.Event> ->
        upstream.publish { shared: Observable<WeatherForecastView.Event> ->
            Observable.mergeArray(
                shared.ofType(WeatherForecastView.Event.OnViewInitialised::class.java),
                shared.ofType(WeatherForecastView.Event.OnLocationPermissionGranted::class.java),
                shared.ofType(WeatherForecastView.Event.OnLocationPermissionDenied::class.java),
                shared.ofType(WeatherForecastView.Event.OnLocationPermissionDeniedNeverAskAgain::class.java),
                shared.ofType(WeatherForecastView.Event.OnSearchButtonToggled::class.java),
                shared.ofType(WeatherForecastView.Event.OnSearchButtonSubmitted::class.java),
                shared.ofType(WeatherForecastView.Event.OnSearchViewDismissed::class.java),
                shared.ofType(WeatherForecastView.Event.OnAutoCompleteItemClicked::class.java),
                shared.ofType(WeatherForecastView.Event.OnStateParcelUpdated::class.java),
                shared.ofType(WeatherForecastView.Event.OnProgressBarEffectStarted::class.java),
                shared.ofType(WeatherForecastView.Event.OnProgressBarEffectStopped::class.java),
                shared.ofType(WeatherForecastView.Event.OnScrollPositionRestored::class.java),
                shared.ofType(WeatherForecastView.Event.OnWeatherListDisplayed::class.java),
                shared.ofType(WeatherForecastView.Event.OnSwipedToRefresh::class.java),
                shared.ofType(WeatherForecastView.Event.OnFailureHandled::class.java),
                shared.ofType(WeatherForecastView.Event.OnSearchTextChanged::class.java)
                    .compose(applyOperatorsOnSearchTextChanged)
            ).compose(
                eventToViewState
            )
        }
    }

    private val applyOperatorsOnSearchTextChanged =
        ObservableTransformer<WeatherForecastView.Event.OnSearchTextChanged,
            WeatherForecastView.Event> { upstream ->
            upstream
                .debounce(AUTOCOMPLETE_DEBOUNCE_MILLIS, TimeUnit.MILLISECONDS)
                .map { event ->
                    event.copy(text = event.text.trim())
                }
                .distinctUntilChanged { previousEvent, currentEvent ->
                    previousEvent.text == currentEvent.text
                }
                .map { it }
        }

    private val eventToViewState = ObservableTransformer<WeatherForecastView.Event,
        WeatherForecastView.State> { upstream: Observable<WeatherForecastView.Event> ->
        upstream.flatMap { event: WeatherForecastView.Event ->
            when (event) {
                is WeatherForecastView.Event.OnViewInitialised -> onViewInitialisedEvent(event)
                WeatherForecastView.Event.OnLocationPermissionGranted -> onLocationPermissionGrantedEvent()
                WeatherForecastView.Event.OnLocationPermissionDenied -> onLocationPermissionDeniedEvent()
                WeatherForecastView.Event.OnLocationPermissionDeniedNeverAskAgain -> onLocationPermissionDeniedNeverAskAgainEvent()
                is WeatherForecastView.Event.OnSearchViewDismissed -> onSearchViewDismissedEvent()
                is WeatherForecastView.Event.OnSearchButtonToggled -> onSearchButtonToggledEvent()
                WeatherForecastView.Event.OnSwipedToRefresh -> onSwipedToRefreshEvent()
                is WeatherForecastView.Event.OnSearchButtonSubmitted -> onSearchButtonSubmittedEvent(event)
                is WeatherForecastView.Event.OnAutoCompleteItemClicked -> onAutoCompleteItemClickedEvent(event)
                WeatherForecastView.Event.OnFailureHandled -> onFailureHandled()
                is WeatherForecastView.Event.OnSearchTextChanged -> onSearchTextChanged(event)
                WeatherForecastView.Event.OnProgressBarEffectStarted -> onProgressBarEffectStartedEvent()
                WeatherForecastView.Event.OnProgressBarEffectStopped -> onProgressBarEffectStoppedEvent()
                WeatherForecastView.Event.OnScrollPositionRestored -> onScrollPositionRestored()
                WeatherForecastView.Event.OnWeatherListDisplayed -> onWeatherListDisplayed()
                WeatherForecastView.Event.OnStateParcelUpdated -> onStateParcelUpdatedEvent()
            }
        }
    }

    private fun onViewInitialisedEvent(event: WeatherForecastView.Event.OnViewInitialised): Observable<WeatherForecastView.State> =
        getAppLocationModeTask()
            .flatMapObservable { result: Either<Failure, Boolean> ->
                result.either(
                    { failure: Failure ->
                        Timber.e("handleRuntimeLocationPermissionMode: Failure: $failure")

                        val errorCode: Int = getErrorCode(failure)
                        val renderEvent: WeatherForecastView.RenderEvent.DisplayError = WeatherForecastView
                            .RenderEvent
                            .DisplayError(errorCode)

                        state = state.copy(renderEvent = renderEvent)
                        state.asObservable()
                    },
                    { locationMode: Boolean ->
                        restoreStateFromStateParcel(event.stateParcel)
                        when (locationMode) {
                            true -> onRequestLocationPermission()
                            false -> onLocationPermissionDeniedEvent()
                        }
                    }
                )
            }

    private fun restoreStateFromStateParcel(stateParcel: WeatherForecastView.StateParcel?) {
        Timber.d("restoreStateFromStateParcel: $stateParcel")

        stateParcel?.run {
            state = state.copy(
                forceNet = forceNet,
                searchText = searchText,
                isSearchToggled = isSearchToggled,
                wasRestoredFromStateParcel = true,
                startRefreshing = startRefreshing,
                stopRefreshing = stopRefreshing
            )
        }
    }

    private fun onStateParcelUpdatedEvent(): Observable<WeatherForecastView.State> {
        state = state.copy(renderEvent = WeatherForecastView.RenderEvent.None)
        return state.asObservable()
    }

    private fun onWeatherListDisplayed(): Observable<WeatherForecastView.State> =
        endLoadingWeatherForecast()

    private fun onProgressBarEffectStartedEvent(): Observable<WeatherForecastView.State> =
        preLoadingWeatherForecast()

    private fun onProgressBarEffectStoppedEvent(): Observable<WeatherForecastView.State> =
        endLoadingWeatherForecast()

    private fun onScrollPositionRestored(): Observable<WeatherForecastView.State> =
        endLoadingWeatherForecast()

    private fun preLoadingWeatherForecast(): Observable<WeatherForecastView.State> =
        when {
            state.isSearchToggled -> {
                val renderEvent: WeatherForecastView.RenderEvent.DisableSearchView =
                    WeatherForecastView.RenderEvent.DisableSearchView(
                        text = String.empty()
                    )
                state = state.copy(
                    renderEvent = renderEvent,
                    searchText = String.empty(),
                    isSearchToggled = false
                )
                state.asObservable()
            }
            state.startRefreshing -> {
                state = state.copy(
                    renderEvent = WeatherForecastView.RenderEvent.StartProgressBarEffect,
                    startRefreshing = false,
                    stopRefreshing = true
                )
                state.asObservable()
            }
            else -> {
                Timber.d("preLoadingWeatherForecast ended")
                state = state.copy(
                    isInitialised = true
                )
                loadWeatherForecast()
            }
        }

    private fun endLoadingWeatherForecast(): Observable<WeatherForecastView.State> =
        when {
            state.isSearchToggled -> {
                state = state.copy(
                    renderEvent = WeatherForecastView
                        .RenderEvent
                        .DisableSearchView(text = String.empty()),
                    isSearchToggled = false
                )
                state.asObservable()
            }
            state.wasRestoredFromStateParcel -> {
                state = state.copy(
                    renderEvent = WeatherForecastView.RenderEvent.RestoreScrollPosition,
                    wasRestoredFromStateParcel = false
                )
                state.asObservable()
            }
            state.stopRefreshing -> {
                state = state.copy(
                    renderEvent = WeatherForecastView.RenderEvent.StopProgressBarEffect,
                    stopRefreshing = false
                )
                state.asObservable()
            }
            else -> {
                Timber.d("endLoadingWeatherForecast ended.")
                updateStateParcel()
                state.asObservable()
            }
        }

    private fun onFailureHandled(): Observable<WeatherForecastView.State> {
        Timber.e("onFailureHandled")
        return endLoadingWeatherForecast()
    }

    private fun onSwipedToRefreshEvent(): Observable<WeatherForecastView.State> {
        Timber.d("onSwipedToRefreshEvent")
        state = state.copy(forceNet = true)
        updateStateToRefreshing()
        return preLoadingWeatherForecast()
    }

    private fun onRequestLocationPermission(): Observable<WeatherForecastView.State> {
        Timber.d("onRequestLocationPermission")
        state = state.copy(
            renderEvent = WeatherForecastView.RenderEvent.RequestLocationPermission
        )
        return state.asObservable()
    }

    private fun updateStateParcel(): Observable<WeatherForecastView.State> {
        state = state.copy(
            renderEvent = WeatherForecastView.RenderEvent.UpdateStateParcel,
            startRefreshing = false,
            stopRefreshing = false,
            forceNet = false
        )
        return state.asObservable()
    }

    private fun onSearchViewDismissedEvent(): Observable<WeatherForecastView.State> =
        preLoadingWeatherForecast()

    private fun onAutoCompleteItemClickedEvent(event: WeatherForecastView.Event.OnAutoCompleteItemClicked): Observable<WeatherForecastView.State> =
        enableGpsLocationModeTask(false)
            .flatMapObservable {
                setLocationInformationTask(event.autoCompleteItem)
                    .flatMapObservable {
                        state = state.copy(
                            forceNet = true
                        )
                        updateStateToRefreshing()
                        preLoadingWeatherForecast()
                    }
            }

    private fun onSearchTextChanged(event: WeatherForecastView.Event.OnSearchTextChanged): Observable<WeatherForecastView.State> =
        getAutoCompleteService(event.text)
            .map { result: Either<Failure, GetAutoCompleteService.Data> ->
                result.either(
                    { failure: Failure ->
                        Timber.e("onSearchTextChanged: $failure")
                        val errorCode: Int = getErrorCode(failure)
                        val renderEvent: WeatherForecastView.RenderEvent.DisplayError = WeatherForecastView.RenderEvent.DisplayError(errorCode)

                        state = state.copy(renderEvent = renderEvent)
                        state
                    },
                    { data: GetAutoCompleteService.Data ->
                        val renderEvent: WeatherForecastView.RenderEvent.DisplayAutoComplete =
                            WeatherForecastView
                                .RenderEvent
                                .DisplayAutoComplete(
                                    data.diffResult,
                                    data.newFilteredList
                                )

                        state = state.copy(
                            renderEvent = renderEvent,
                            searchText = event.text
                        )
                        state
                    }
                )
            }.toObservable()

    private fun onSearchButtonSubmittedEvent(event: WeatherForecastView.Event.OnSearchButtonSubmitted): Observable<WeatherForecastView.State> =
        getAutoCompleteService(event.query)
            .flatMapObservable { result: Either<Failure, GetAutoCompleteService.Data> ->
                result.either(
                    { failure: Failure ->
                        Timber.e("getAutoCompleteService failed: $failure")
                        val errorCode: Int = getErrorCode(failure)
                        val renderEvent: WeatherForecastView.RenderEvent.DisplayError = WeatherForecastView.RenderEvent.DisplayError(errorCode)

                        state = state.copy(renderEvent = renderEvent)
                        state.asObservable()
                    },
                    { data: GetAutoCompleteService.Data ->
                        Timber.d("Submit triggered: ${data.newFilteredList.first()}")
                        enableGpsLocationModeTask(false).flatMap {
                            setLocationInformationTask(data.newFilteredList.first())
                        }.flatMapObservable {
                            updateStateToRefreshing()
                            state = state.copy(
                                forceNet = true,
                                searchText = String.empty(),
                                isSearchToggled = true
                            )
                            preLoadingWeatherForecast()
                        }
                    }
                )
            }

    private fun setDefaultLocationInformation(): Observable<WeatherForecastView.State> =
        setDefaultLocationInformationTask()
            .flatMapObservable { result: Either<Failure, Unit> ->
                result.either(
                    { failure: Failure ->
                        Timber.e("setDefaultLocationInformation: $failure")
                        val errorCode = getErrorCode(failure)
                        val renderEvent = WeatherForecastView.RenderEvent.DisplayError(errorCode)

                        state = state.copy(renderEvent = renderEvent)
                        state.asObservable()
                    },
                    {
                        preLoadingWeatherForecast()
                    }
                )
            }

    /**
     *  On location mode setting updated. Which happens after
     *  location permission is successfully granted.
     */
    private fun loadWeatherForecast(): Observable<WeatherForecastView.State> =
        getWeatherForecastLastCheckedTask()
            .flatMapObservable { result: Either<Failure, Long> ->
                result.either(
                    { failure: Failure ->
                        Timber.e("Failure: $failure")
                        state.asObservable()
                    },
                    { timeStamp: Long ->
                        getWeatherForecastService(timeStamp, state.forceNet)
                            .flatMapObservable { result: Either<Failure, GetWeatherForecastService.Data> ->
                                result.either(
                                    { failure: Failure ->
                                        Timber.e("loadWeatherForecastFailure: $failure")
                                        val errorCode: Int = getErrorCode(failure)
                                        val renderEvent: WeatherForecastView.RenderEvent.DisplayError = WeatherForecastView.RenderEvent.DisplayError(errorCode)

                                        state = state.copy(renderEvent = renderEvent)
                                        state.asObservable()
                                    },
                                    { data: GetWeatherForecastService.Data ->

                                        val renderEvent: WeatherForecastView.RenderEvent.DisplayWeatherForecast =
                                            WeatherForecastView.RenderEvent.DisplayWeatherForecast(
                                                list = data.weatherForecastModelList,
                                                locality = Locality(name = data.localityName)
                                            )

                                        state = state.copy(
                                            renderEvent = renderEvent
                                        )
                                        updateLastChecked(data.timeStamp)
                                    }
                                )
                            }
                    }
                )
            }

    private fun updateLastChecked(timeStamp: Long): Observable<WeatherForecastView.State> =
        setWeatherForecastLastCheckedTask(value = timeStamp)
            .flatMapObservable { result: Either<Failure, Unit> ->
                result.either(
                    { failure: Failure ->
                        Timber.e("Failure: $failure")
                        state.asObservable()
                    },
                    {
                        Timber.d("LAST CHECKED UPDATED")
                        state.asObservable()
                    }
                )
            }

    private fun onSearchButtonToggledEvent(): Observable<WeatherForecastView.State> {
        state = state.copy(
            renderEvent = WeatherForecastView.RenderEvent.EnableSearchView,
            isSearchToggled = true
        )
        return state.asObservable()
    }

    private fun updateStateToLoadingAndRefreshing(): Observable<WeatherForecastView.State> {
        Timber.d("updateStateToLoadingAndRefreshing")
        state = state.copy(
            startRefreshing = state.wasRestoredFromStateParcel.not()
        )
        return state.asObservable()
    }

    private fun updateStateToRefreshing(): Observable<WeatherForecastView.State> {
        Timber.d("updateStateToRefreshing")
        state = state.copy(
            startRefreshing = true
        )
        return state.asObservable()
    }

    private fun onLocationPermissionGrantedEvent(): Observable<WeatherForecastView.State> =
        enableGpsLocationModeTask(true)
            .flatMapObservable { result: Either<Failure, Unit> ->
                result.either(
                    { failure: Failure ->
                        Timber.e("onLocationPermissionGrantedFailure: $failure")
                        val errorCode = getErrorCode(failure)
                        val renderEvent = WeatherForecastView.RenderEvent.DisplayError(errorCode)

                        state = state.copy(renderEvent = renderEvent)
                        state.asObservable()
                    },
                    {
                        Timber.d("Location mode updated to true -- proceeding to fetch weather.")
                        updateStateToLoadingAndRefreshing()
                        preLoadingWeatherForecast()
                    }
                )
            }

    private fun onLocationPermissionDeniedEvent(): Observable<WeatherForecastView.State> =
        getAppLocationModeTask()
            .flatMapObservable { result: Either<Failure, Boolean> ->
                result.either(
                    { failure: Failure ->
                        Timber.e("onLocationPermissionDeniedEvent: $failure")
                        val errorCode = getErrorCode(failure)
                        val renderEvent = WeatherForecastView
                            .RenderEvent
                            .DisplayError(errorCode)

                        state = state.copy(renderEvent = renderEvent)
                        state.asObservable()
                    },
                    { locationMode: Boolean ->
                        updateStateToLoadingAndRefreshing()
                        when (locationMode) {
                            false -> preLoadingWeatherForecast()
                            true -> setDefaultLocationInformation()
                        }
                    }
                )
            }

    private fun onLocationPermissionDeniedNeverAskAgainEvent(): Observable<WeatherForecastView.State> =
        enableGpsLocationModeTask(false).flatMapObservable { result ->
            result.either(
                { failure: Failure ->
                    Timber.e("onLocationPermissionDeniedNeverAskAgainEvent: $failure")
                    val errorCode: Int = getErrorCode(failure)
                    val renderEvent: WeatherForecastView.RenderEvent.DisplayError = WeatherForecastView.RenderEvent.DisplayError(errorCode)

                    state = state.copy(renderEvent = renderEvent)
                    state.asObservable()
                },
                {
                    Timber.d("Location mode updated, proceeding to fetch weather.")
                    updateStateToLoadingAndRefreshing()
                    preLoadingWeatherForecast()
                }
            )
        }
}