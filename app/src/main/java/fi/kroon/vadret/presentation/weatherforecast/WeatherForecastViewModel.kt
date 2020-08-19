package fi.kroon.vadret.presentation.weatherforecast

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
import fi.kroon.vadret.presentation.weatherforecast.di.WeatherForecastScope
import fi.kroon.vadret.util.extension.empty
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.rx2.await
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject

@FlowPreview
@ExperimentalCoroutinesApi
@WeatherForecastScope
class WeatherForecastViewModel @Inject constructor(
    private var state: WeatherForecastView.State,
    private val eventChannel: ConflatedBroadcastChannel<WeatherForecastView.Event>,
    private val setDefaultLocationInformationTask: SetDefaultLocationInformationTask,
    private val getWeatherForecastService: GetWeatherForecastService,
    private val getAutoCompleteService: GetAutoCompleteService,
    private val setLocationInformationTask: SetLocationInformationTask,
    private val enableGpsLocationModeTask: SetLocationModeTask,
    private val getAppLocationModeTask: GetAppLocationModeTask,
    private val setWeatherForecastLastCheckedTask: SetWeatherForecastLastCheckedTask,
    private val getWeatherForecastLastCheckedTask: GetWeatherForecastLastCheckedTask
) : IViewModel {

    operator fun invoke(): Flow<WeatherForecastView.State> =
        eventChannel
            .asFlow()
            .map { event: WeatherForecastView.Event ->
                reduce(event)
            }

    private suspend fun reduce(event: WeatherForecastView.Event): WeatherForecastView.State {
        Timber.d("event: $event")
        return when (event) {
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

    private suspend fun onViewInitialisedEvent(stateParcel: WeatherForecastView.StateParcel?): WeatherForecastView.State =
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
                        state
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

    private fun onStateParcelUpdatedEvent(): WeatherForecastView.State {
        state = state.copy(renderEvent = WeatherForecastView.RenderEvent.Idle)
        return state
    }

    private fun onWeatherListDisplayed(): WeatherForecastView.State =
        endLoadingWeatherForecast()

    private suspend fun onProgressBarEffectStartedEvent(): WeatherForecastView.State =
        preLoadingWeatherForecast()

    private fun onProgressBarEffectStoppedEvent(): WeatherForecastView.State =
        endLoadingWeatherForecast()

    private fun onScrollPositionRestored(): WeatherForecastView.State =
        endLoadingWeatherForecast()

    private suspend fun preLoadingWeatherForecast(): WeatherForecastView.State =
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
                    state
                }
                state.startRefreshing -> {
                    state = state.copy(
                        renderEvent = WeatherForecastView.RenderEvent.StartProgressBarEffect,
                        startRefreshing = false,
                        stopRefreshing = true
                    )
                    state
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

    private fun endLoadingWeatherForecast(): WeatherForecastView.State =
        when {
            state.isSearchToggled -> {
                state = state.copy(
                    renderEvent = WeatherForecastView
                        .RenderEvent
                        .DisableSearchView(text = String.empty()),
                    isSearchToggled = false
                )
                state
            }
            state.wasRestoredFromStateParcel -> {
                state = state.copy(
                    renderEvent = WeatherForecastView.RenderEvent.RestoreScrollPosition,
                    wasRestoredFromStateParcel = false
                )
                state
            }
            state.stopRefreshing -> {
                state = state.copy(
                    renderEvent = WeatherForecastView.RenderEvent.StopProgressBarEffect,
                    stopRefreshing = false
                )
                state
            }
            else -> {
                Timber.d("endLoadingWeatherForecast ended.")
                updateStateParcel()
                state
            }
        }

    private fun onFailureHandled(): WeatherForecastView.State {
        Timber.e("onFailureHandled")
        return endLoadingWeatherForecast()
    }

    private suspend fun onSwipedToRefreshEvent(): WeatherForecastView.State {
        Timber.d("onSwipedToRefreshEvent")
        state = state.copy(forceNet = true)
        updateStateToRefreshing()
        return preLoadingWeatherForecast()
    }

    private fun onRequestLocationPermission(): WeatherForecastView.State {
        Timber.d("onRequestLocationPermission")
        state = state.copy(
            renderEvent = WeatherForecastView.RenderEvent.RequestLocationPermission
        )
        return state
    }

    private fun updateStateParcel(): WeatherForecastView.State {
        state = state.copy(
            renderEvent = WeatherForecastView.RenderEvent.UpdateStateParcel,
            startRefreshing = false,
            stopRefreshing = false,
            forceNet = false
        )
        return state
    }

    private suspend fun onSearchViewDismissedEvent(): WeatherForecastView.State =
        preLoadingWeatherForecast()

    private suspend fun onAutoCompleteItemClickedEvent(autoCompleteItem: AutoCompleteItem): WeatherForecastView.State =
        enableGpsLocationModeTask(false)
            .await()
            .either(
                {
                    Timber.e("$it")
                    state
                },
                {
                    updateLocationInformation(autoCompleteItem = autoCompleteItem)
                }
            )

    private suspend fun updateLocationInformation(autoCompleteItem: AutoCompleteItem): WeatherForecastView.State =
        setLocationInformationTask(autoCompleteItem)
            .await()
            ?.either(
                {
                    Timber.e("$it")
                    state
                },
                {
                    state = state.copy(forceNet = true)
                    updateStateToRefreshing()
                    preLoadingWeatherForecast()
                }
            ) ?: state

    private suspend fun onSearchTextChanged(searchText: String): WeatherForecastView.State =
        getAutoCompleteService(searchText)
            .await()
            ?.either(
                { failure: Failure ->
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
                        searchText = searchText
                    )
                    state
                }
            ) ?: state

    private suspend fun onSearchButtonSubmittedEvent(query: String): WeatherForecastView.State =
        getAutoCompleteService(query)
            .await()
            ?.either(
                { failure: Failure ->
                    Timber.e("getAutoCompleteService failed: $failure")
                    val errorCode: Int = getErrorCode(failure)
                    val renderEvent: WeatherForecastView.RenderEvent.DisplayError = WeatherForecastView.RenderEvent.DisplayError(errorCode)

                    state = state.copy(renderEvent = renderEvent)
                    state
                },
                { data: GetAutoCompleteService.Data ->
                    Timber.d("Submit triggered: ${data.newFilteredList.first()}")
                    enableGpsLocationModeTask(false)
                        .await()
                        .either(
                            {
                                state
                            },
                            {
                                setLocationInformation(data.newFilteredList.first())
                                state
                            }
                        )
                }
            ) ?: state

    private suspend fun setLocationInformation(autoCompleteItem: AutoCompleteItem): WeatherForecastView.State =
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

    private suspend fun setDefaultLocationInformation(): WeatherForecastView.State =
        setDefaultLocationInformationTask()
            .await()
            .either(
                { failure: Failure ->
                    Timber.e("setDefaultLocationInformation: $failure")
                    val errorCode = getErrorCode(failure)
                    val renderEvent = WeatherForecastView.RenderEvent.DisplayError(errorCode)

                    state = state.copy(renderEvent = renderEvent)
                    state
                },
                {
                    preLoadingWeatherForecast()
                }
            )

    /**
     *  On location mode setting updated. Which happens after
     *  location permission is successfully granted.
     */
    private suspend fun loadWeatherForecast(): WeatherForecastView.State =
        getWeatherForecastLastCheckedTask()
            .await()
            .either(
                { failure: Failure ->
                    Timber.e("Failure: $failure")
                    state
                },
                { timeStamp: Long ->
                    getWeatherForecastService(timeStamp, state.forceNet)
                        .await()
                        .either(
                            { failure: Failure ->
                                Timber.e("loadWeatherForecastFailure: $failure")
                                val errorCode: Int = getErrorCode(failure)
                                val renderEvent: WeatherForecastView.RenderEvent.DisplayError =
                                    WeatherForecastView.RenderEvent.DisplayError(errorCode)

                                state = state.copy(renderEvent = renderEvent)
                                state
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

    private suspend fun updateLastChecked(timeStamp: Long): WeatherForecastView.State =
        setWeatherForecastLastCheckedTask(value = timeStamp)
            .await()
            .either(
                { failure: Failure ->
                    Timber.e("Failure: $failure")
                    state
                },
                {
                    Timber.d("LAST CHECKED UPDATED")
                    state
                }
            )

    private fun onSearchButtonToggledEvent(): WeatherForecastView.State {
        state = state.copy(
            renderEvent = WeatherForecastView.RenderEvent.EnableSearchView,
            isSearchToggled = true
        )
        return state
    }

    private fun updateStateToLoadingAndRefreshing(): WeatherForecastView.State {
        Timber.d("updateStateToLoadingAndRefreshing")
        state = state.copy(
            startRefreshing = state.wasRestoredFromStateParcel.not()
        )
        return state
    }

    private fun updateStateToRefreshing(): WeatherForecastView.State {
        Timber.d("updateStateToRefreshing")
        state = state.copy(
            startRefreshing = true
        )
        return state
    }

    private suspend fun onLocationPermissionGrantedEvent(): WeatherForecastView.State =
        enableGpsLocationModeTask(true)
            .await()
            ?.either(
                { failure: Failure ->
                    Timber.e("onLocationPermissionGrantedFailure: $failure")
                    val errorCode = getErrorCode(failure)
                    val renderEvent = WeatherForecastView.RenderEvent.DisplayError(errorCode)

                    state = state.copy(renderEvent = renderEvent)
                    state
                },
                {
                    Timber.d("Location mode updated to true -- proceeding to fetch weather.")
                    updateStateToLoadingAndRefreshing()
                    preLoadingWeatherForecast()
                }
            ) ?: state

    private suspend fun onLocationPermissionDeniedEvent(): WeatherForecastView.State =
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
                    state
                },
                { locationMode: Boolean ->
                    updateStateToLoadingAndRefreshing()
                    when (locationMode) {
                        false -> preLoadingWeatherForecast()
                        true -> setDefaultLocationInformation()
                    }
                }
            )

    private suspend fun onLocationPermissionDeniedNeverAskAgainEvent(): WeatherForecastView.State =
        enableGpsLocationModeTask(false)
            .await()
            .either(
                { failure: Failure ->
                    Timber.e("onLocationPermissionDeniedNeverAskAgainEvent: $failure")
                    val errorCode: Int = getErrorCode(failure)
                    val renderEvent: WeatherForecastView.RenderEvent.DisplayError = WeatherForecastView.RenderEvent.DisplayError(errorCode)

                    state = state.copy(renderEvent = renderEvent)
                    state
                },
                {
                    Timber.d("Location mode updated, proceeding to fetch weather.")
                    updateStateToLoadingAndRefreshing()
                    preLoadingWeatherForecast()
                }
            )
}