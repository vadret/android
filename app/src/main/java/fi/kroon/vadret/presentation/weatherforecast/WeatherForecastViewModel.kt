package fi.kroon.vadret.presentation.weatherforecast

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import fi.kroon.vadret.data.autocomplete.model.AutoCompleteItem
import fi.kroon.vadret.data.failure.Failure
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
import fi.kroon.vadret.util.extension.empty
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.rx2.await
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject

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
) : IViewModel, ViewModel() {

    fun send(event: WeatherForecastView.Event) {
        viewModelScope.launch {
            reduce(event = event)
        }
    }

    private val mutState: MutableSharedFlow<WeatherForecastView.State> = MutableSharedFlow()

    val viewState = mutState.asSharedFlow()

    private suspend fun reduce(event: WeatherForecastView.Event) = withContext(Dispatchers.IO) {
        Timber.d("event: $event")
        when (event) {
            is WeatherForecastView.Event.OnViewInitialised -> onViewInitialisedEvent(event.stateParcel)
            WeatherForecastView.Event.OnLocationPermissionGranted -> onLocationPermissionGrantedEvent()
            WeatherForecastView.Event.OnLocationPermissionDenied -> onLocationPermissionDeniedEvent()
            WeatherForecastView.Event.OnLocationPermissionDeniedNeverAskAgain -> onLocationPermissionDeniedNeverAskAgainEvent()
            is WeatherForecastView.Event.OnSearchViewDismissed -> onSearchViewDismissedEvent()
            is WeatherForecastView.Event.OnSearchButtonToggled -> onSearchButtonToggledEvent()
            WeatherForecastView.Event.OnSwipedToRefresh -> onSwipedToRefreshEvent()
            is WeatherForecastView.Event.OnSearchButtonSubmitted -> onSearchButtonSubmittedEvent(event.query)
            is WeatherForecastView.Event.OnAutoCompleteItemClicked -> onAutoCompleteItemClickedEvent(event.autoCompleteItem)
            WeatherForecastView.Event.OnFailureHandled -> onFailureHandled()
            is WeatherForecastView.Event.OnSearchTextChanged -> onSearchTextChanged(event.text)
            WeatherForecastView.Event.OnProgressBarEffectStarted -> onProgressBarEffectStartedEvent()
            WeatherForecastView.Event.OnProgressBarEffectStopped -> onProgressBarEffectStoppedEvent()
            WeatherForecastView.Event.OnScrollPositionRestored -> onScrollPositionRestored()
            WeatherForecastView.Event.OnWeatherListDisplayed -> onWeatherListDisplayed()
            WeatherForecastView.Event.OnStateParcelUpdated -> onStateParcelUpdatedEvent()
        }
    }

    private suspend fun onViewInitialisedEvent(stateParcel: WeatherForecastView.StateParcel?) =
        withContext(Dispatchers.IO) {
            getAppLocationModeTask()
                .await()
                .either(
                    { failure: Failure ->
                        Timber.e("handleRuntimeLocationPermissionMode: Failure: $failure")
                        val errorCode: Int = getErrorCode(failure)
                        val renderEvent: WeatherForecastView.RenderEvent.DisplayError = WeatherForecastView
                            .RenderEvent
                            .DisplayError(errorCode)

                        state = state.copy(renderEvent = renderEvent)
                        mutState.emit(state)
                    },
                    { locationMode: Boolean ->
                        restoreStateFromStateParcel(stateParcel)
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

    private suspend fun onStateParcelUpdatedEvent() {
        state = state.copy(renderEvent = WeatherForecastView.RenderEvent.Idle)
        mutState.emit(state)
    }

    private suspend fun onWeatherListDisplayed() =
        endLoadingWeatherForecast()

    private suspend fun onProgressBarEffectStartedEvent() =
        preLoadingWeatherForecast()

    private suspend fun onProgressBarEffectStoppedEvent() =
        endLoadingWeatherForecast()

    private suspend fun onScrollPositionRestored() =
        endLoadingWeatherForecast()

    private suspend fun preLoadingWeatherForecast() =
        withContext(Dispatchers.IO) {
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
                    mutState.emit(state)
                }
                state.startRefreshing -> {
                    state = state.copy(
                        renderEvent = WeatherForecastView.RenderEvent.StartProgressBarEffect,
                        startRefreshing = false,
                        stopRefreshing = true
                    )
                    mutState.emit(state)
                }
                else -> {
                    Timber.d("preLoadingWeatherForecast ended")
                    state = state.copy(
                        isInitialised = true
                    )
                    loadWeatherForecast()
                }
            }
        }

    private suspend fun endLoadingWeatherForecast() =
        when {
            state.isSearchToggled -> {
                state = state.copy(
                    renderEvent = WeatherForecastView
                        .RenderEvent
                        .DisableSearchView(text = String.empty()),
                    isSearchToggled = false
                )
                mutState.emit(state)
            }
            state.wasRestoredFromStateParcel -> {
                state = state.copy(
                    renderEvent = WeatherForecastView.RenderEvent.RestoreScrollPosition,
                    wasRestoredFromStateParcel = false
                )
                mutState.emit(state)
            }
            state.stopRefreshing -> {
                state = state.copy(
                    renderEvent = WeatherForecastView.RenderEvent.StopProgressBarEffect,
                    stopRefreshing = false
                )
                mutState.emit(state)
            }
            else -> {
                Timber.d("endLoadingWeatherForecast ended.")
                updateStateParcel()
                mutState.emit(state)
            }
        }

    private suspend fun onFailureHandled() {
        Timber.e("onFailureHandled")
        endLoadingWeatherForecast()
    }

    private suspend fun onSwipedToRefreshEvent() {
        Timber.d("onSwipedToRefreshEvent")
        state = state.copy(forceNet = true)
        updateStateToRefreshing()
        preLoadingWeatherForecast()
    }

    private suspend fun onRequestLocationPermission() {
        Timber.d("onRequestLocationPermission")
        state = state.copy(
            renderEvent = WeatherForecastView.RenderEvent.RequestLocationPermission
        )
        mutState.emit(state)
    }

    private suspend fun updateStateParcel() {
        state = state.copy(
            renderEvent = WeatherForecastView.RenderEvent.UpdateStateParcel,
            startRefreshing = false,
            stopRefreshing = false,
            forceNet = false
        )
        mutState.emit(state)
    }

    private suspend fun onSearchViewDismissedEvent() =
        preLoadingWeatherForecast()

    private suspend fun onAutoCompleteItemClickedEvent(autoCompleteItem: AutoCompleteItem) =
        enableGpsLocationModeTask(false)
            .await()
            .either(
                {
                    Timber.e("$it")
                    mutState.emit(state)
                },
                {
                    updateLocationInformation(autoCompleteItem = autoCompleteItem)
                }
            )

    private suspend fun updateLocationInformation(autoCompleteItem: AutoCompleteItem) =
        setLocationInformationTask(autoCompleteItem)
            .await()
            ?.either(
                {
                    Timber.e("$it")
                    mutState.emit(state)
                },
                {
                    state = state.copy(forceNet = true)
                    updateStateToRefreshing()
                    preLoadingWeatherForecast()
                }
            )

    private suspend fun onSearchTextChanged(searchText: String) =
        getAutoCompleteService(searchText)
            .await()
            ?.either(
                { failure: Failure ->
                    val errorCode: Int = getErrorCode(failure)
                    val renderEvent: WeatherForecastView.RenderEvent.DisplayError = WeatherForecastView.RenderEvent.DisplayError(errorCode)

                    state = state.copy(renderEvent = renderEvent)
                    mutState.emit(state)
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
                        searchText = searchText
                    )
                    mutState.emit(state)
                }
            )

    private suspend fun onSearchButtonSubmittedEvent(query: String) =
        getAutoCompleteService(query)
            .await()
            ?.either(
                { failure: Failure ->
                    Timber.e("getAutoCompleteService failed: $failure")
                    val errorCode: Int = getErrorCode(failure)
                    val renderEvent: WeatherForecastView.RenderEvent.DisplayError =
                        WeatherForecastView.RenderEvent.DisplayError(errorCode)

                    state = state.copy(renderEvent = renderEvent)
                    mutState.emit(state)
                },
                { data: GetAutoCompleteService.Data ->
                    Timber.d("Submit triggered: ${data.newFilteredList.first()}")
                    enableGpsLocationModeTask(false)
                        .await()
                        .either(
                            {
                                mutState.emit(state)
                            },
                            {
                                setLocationInformation(data.newFilteredList.first())
                                mutState.emit(state)
                            }
                        )
                }
            )

    private suspend fun setLocationInformation(autoCompleteItem: AutoCompleteItem) =
        setLocationInformationTask(autoCompleteItem)
            .await()
            .either(
                {
                    state
                },
                {
                    updateStateToRefreshing()
                    state = state.copy(
                        forceNet = true,
                        searchText = String.empty(),
                        isSearchToggled = true
                    )
                    preLoadingWeatherForecast()
                }
            )

    private suspend fun setDefaultLocationInformation() =
        setDefaultLocationInformationTask()
            .await()
            .either(
                { failure: Failure ->
                    Timber.e("setDefaultLocationInformation: $failure")
                    val errorCode = getErrorCode(failure)
                    val renderEvent = WeatherForecastView.RenderEvent.DisplayError(errorCode)

                    state = state.copy(renderEvent = renderEvent)
                    mutState.emit(state)
                },
                {
                    preLoadingWeatherForecast()
                }
            )

    /**
     *  On location mode setting updated. Which happens after
     *  location permission is successfully granted.
     */
    private suspend fun loadWeatherForecast() =
        getWeatherForecastLastCheckedTask()
            .await()
            .either(
                { failure: Failure ->
                    Timber.e("Failure: $failure")
                    mutState.emit(state)
                },
                { timeStamp: Long ->
                    getWeatherForecastService(timeStamp, state.forceNet)
                        .either(
                            { failure: Failure ->
                                Timber.e("loadWeatherForecastFailure: $failure")
                                val errorCode: Int = getErrorCode(failure)
                                val renderEvent: WeatherForecastView.RenderEvent.DisplayError =
                                    WeatherForecastView.RenderEvent.DisplayError(errorCode)

                                state = state.copy(renderEvent = renderEvent)
                                mutState.emit(state)
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
            )

    private suspend fun updateLastChecked(timeStamp: Long) =
        setWeatherForecastLastCheckedTask(value = timeStamp)
            .await()
            .either(
                { failure: Failure ->
                    Timber.e("Failure: $failure")
                    mutState.emit(state)
                },
                {
                    Timber.d("LAST CHECKED UPDATED")
                    mutState.emit(state)
                }
            )

    private suspend fun onSearchButtonToggledEvent() {
        state = state.copy(
            renderEvent = WeatherForecastView.RenderEvent.EnableSearchView,
            isSearchToggled = true
        )
        mutState.emit(state)
    }

    private suspend fun updateStateToLoadingAndRefreshing() {
        Timber.d("updateStateToLoadingAndRefreshing")
        state = state.copy(
            startRefreshing = state.wasRestoredFromStateParcel.not()
        )
        mutState.emit(state)
    }

    private suspend fun updateStateToRefreshing() {
        Timber.d("updateStateToRefreshing")
        state = state.copy(
            startRefreshing = true
        )
        mutState.emit(state)
    }

    private suspend fun onLocationPermissionGrantedEvent() =
        enableGpsLocationModeTask(true)
            .await()
            ?.either(
                { failure: Failure ->
                    Timber.e("onLocationPermissionGrantedFailure: $failure")
                    val errorCode = getErrorCode(failure)
                    val renderEvent = WeatherForecastView.RenderEvent.DisplayError(errorCode)

                    state = state.copy(renderEvent = renderEvent)
                    mutState.emit(state)
                },
                {
                    Timber.d("Location mode updated to true -- proceeding to fetch weather.")
                    updateStateToLoadingAndRefreshing()
                    preLoadingWeatherForecast()
                }
            )

    private suspend fun onLocationPermissionDeniedEvent() =
        getAppLocationModeTask()
            .await()
            .either(
                { failure: Failure ->
                    Timber.e("onLocationPermissionDeniedEvent: $failure")
                    val errorCode = getErrorCode(failure)
                    val renderEvent = WeatherForecastView
                        .RenderEvent
                        .DisplayError(errorCode)

                    state = state.copy(renderEvent = renderEvent)
                    mutState.emit(state)
                },
                { locationMode: Boolean ->
                    updateStateToLoadingAndRefreshing()
                    when (locationMode) {
                        false -> preLoadingWeatherForecast()
                        true -> setDefaultLocationInformation()
                    }
                }
            )

    private suspend fun onLocationPermissionDeniedNeverAskAgainEvent() =
        enableGpsLocationModeTask(false)
            .await()
            .either(
                { failure: Failure ->
                    Timber.e("onLocationPermissionDeniedNeverAskAgainEvent: $failure")
                    val errorCode: Int = getErrorCode(failure)
                    val renderEvent: WeatherForecastView.RenderEvent.DisplayError = WeatherForecastView.RenderEvent.DisplayError(errorCode)

                    state = state.copy(renderEvent = renderEvent)
                    mutState.emit(state)
                },
                {
                    Timber.d("Location mode updated, proceeding to fetch weather.")
                    updateStateToLoadingAndRefreshing()
                    preLoadingWeatherForecast()
                }
            )
}