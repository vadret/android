package fi.kroon.vadret.weatherforecast

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import fi.kroon.vadret.data.autocomplete.model.AutoCompleteItem
import fi.kroon.vadret.data.failure.Failure
import fi.kroon.vadret.data.location.model.Location
import fi.kroon.vadret.data.weatherforecast.model.Weather
import fi.kroon.vadret.data.weatherforecast.model.WeatherOut
import fi.kroon.vadret.shared.IViewModel
import fi.kroon.vadret.util.AUTOCOMPLETE_DEBOUNCE_MILLIS
import fi.kroon.vadret.weatherforecast.model.SearchState
import fi.kroon.vadret.weatherforecast.model.WeatherForecast
import io.github.sphrak.either.Either
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
import java.util.Locale
import javax.inject.Inject
import kotlin.time.Clock

class WeatherForecastViewModel @Inject constructor(
    private val getAppLocationModeTask: GetAppLocationModeTask,
    private val setLocationModeTask: SetLocationModeTask,
    private val setLocationInformationTask: SetLocationInformationTask,
    private val getWeatherForecastService: GetWeatherForecastService,
    private val getWeatherForecastLastCheckedTask: GetWeatherForecastLastCheckedTask,
    private val setWeatherForecastLastCheckedTask: SetWeatherForecastLastCheckedTask,
    private val getAutoCompleteService: GetAutoCompleteService,
    private val clock: Clock,
    private val coroutineDispatcher: CoroutineDispatcher
) : ViewModel(), IViewModel {

    private val mutableState: MutableStateFlow<WeatherForecastState> = MutableStateFlow(WeatherForecastState())
    val state: StateFlow<WeatherForecastState> = mutableState.asStateFlow()

    /**
     *  The location mode the shown forecast was loaded with.
     */
    private val loadedLocationMode: MutableStateFlow<Boolean?> = MutableStateFlow(null)
    private var loadJob: Job? = null
    private var searchJob: Job? = null

    fun onEvent(event: WeatherForecastEvent) {
        when (event) {
            WeatherForecastEvent.OnShown -> onShown()
            WeatherForecastEvent.OnRefresh -> onRefresh()
            WeatherForecastEvent.OnErrorShown -> mutableState.update { state: WeatherForecastState ->
                state.copy(errorMessageResourceId = null)
            }
            is WeatherForecastEvent.OnLocationPermissionResult -> onLocationPermissionResult(isGranted = event.isGranted)
            WeatherForecastEvent.OnSearchButtonClicked -> mutableState.update { state: WeatherForecastState ->
                state.copy(search = SearchState())
            }
            WeatherForecastEvent.OnSearchDismissed -> onSearchDismissed()
            WeatherForecastEvent.OnSearchSubmitted -> onSearchSubmitted()
            is WeatherForecastEvent.OnSearchQueryChanged -> onSearchQueryChanged(query = event.query)
            is WeatherForecastEvent.OnSuggestionClicked -> onSuggestionClicked(autoCompleteItem = event.autoCompleteItem)
        }
    }

    private fun onShown() {
        viewModelScope.launch(context = coroutineDispatcher) {
            getAppLocationModeTask()
                .onResult(
                    { failure: Failure ->
                        displayError(failure = failure)
                    },
                    { isAutomatic: Boolean ->
                        if (isAutomatic != loadedLocationMode.value) {
                            startLoading(isAutomatic = isAutomatic)
                        }
                    }
                )
        }
    }

    private fun startLoading(isAutomatic: Boolean) {
        if (isAutomatic) {
            mutableState.update { state: WeatherForecastState ->
                state.copy(isLocationPermissionRequested = true)
            }
        } else {
            load(
                isAutomatic = false,
                forceNet = false
            )
        }
    }

    /**
     *  Without location access the stored place is used,
     *  which is Stockholm until the user searches for another.
     */
    private fun onLocationPermissionResult(isGranted: Boolean) {
        mutableState.update { state: WeatherForecastState ->
            state.copy(isLocationPermissionRequested = false)
        }
        viewModelScope.launch(context = coroutineDispatcher) {
            setLocationModeTask(value = isGranted)
                .onResult(
                    { failure: Failure ->
                        displayError(failure = failure)
                    },
                    {
                        load(
                            isAutomatic = isGranted,
                            forceNet = false
                        )
                    }
                )
        }
    }

    /**
     *  Nothing to refresh until the first load has started.
     */
    private fun onRefresh() {
        val isAutomatic: Boolean? = loadedLocationMode.value
        if (isAutomatic == null) {
            return
        }
        mutableState.update { state: WeatherForecastState ->
            state.copy(isRefreshing = true)
        }
        load(
            isAutomatic = isAutomatic,
            forceNet = true
        )
    }

    private fun load(isAutomatic: Boolean, forceNet: Boolean) {
        loadedLocationMode.value = isAutomatic
        loadJob?.cancel()
        loadJob = viewModelScope.launch(context = coroutineDispatcher) {
            getWeatherForecastLastCheckedTask()
                .flatMap { timeStamp: Long ->
                    getWeatherForecastService(
                        timeStamp = timeStamp,
                        forceNet = forceNet
                    )
                }
                .onResult(
                    { failure: Failure ->
                        displayError(failure = failure)
                    },
                    { data: GetWeatherForecastService.Data ->
                        displayForecast(data = data)
                        setLastChecked(timeStamp = data.timeStamp)
                    }
                )
        }
    }

    private fun displayForecast(data: GetWeatherForecastService.Data) {
        val weather: Weather? = data.weather
        val weatherOut: WeatherOut? = data.weatherOut
        if (weather == null || weatherOut == null) {
            return
        }
        val forecast: WeatherForecast = WeatherForecastMapper(
            timeSeries = weather.timeSeries,
            location = Location(
                latitude = weatherOut.latitude,
                longitude = weatherOut.longitude
            ),
            now = clock.now(),
            timeZone = TimeZone.currentSystemDefault(),
            locale = Locale.getDefault()
        )
        mutableState.update { state: WeatherForecastState ->
            state.copy(
                forecast = forecast,
                localityName = data.localityName,
                isLoading = false,
                isRefreshing = false
            )
        }
    }

    /**
     *  A lost timestamp only means the next load skips the cache.
     */
    private fun setLastChecked(timeStamp: Long) {
        setWeatherForecastLastCheckedTask(value = timeStamp)
            .onResult(
                { failure: Failure ->
                    Timber.e("Failed storing last checked: $failure")
                },
                {}
            )
    }

    private fun onSearchDismissed() {
        searchJob?.cancel()
        mutableState.update { state: WeatherForecastState ->
            state.copy(search = null)
        }
    }

    /**
     *  The query is stored right away so the text field
     *  never lags behind typing, suggestions follow.
     */
    private fun onSearchQueryChanged(query: String) {
        mutableState.update { state: WeatherForecastState ->
            val search: SearchState? = state.search
            if (search != null) {
                state.copy(search = search.copy(query = query))
            } else {
                state
            }
        }
        searchJob?.cancel()
        searchJob = viewModelScope.launch(context = coroutineDispatcher) {
            delay(timeMillis = AUTOCOMPLETE_DEBOUNCE_MILLIS)
            val suggestions: Either<Failure, List<AutoCompleteItem>> = if (query.isBlank()) {
                Either.Right(emptyList())
            } else {
                getAutoCompleteService(text = query)
            }
            suggestions.onResult(
                { failure: Failure ->
                    displayError(failure = failure)
                },
                { autoCompleteItemList: List<AutoCompleteItem> ->
                    mutableState.update { state: WeatherForecastState ->
                        val search: SearchState? = state.search
                        if (search != null && search.query == query) {
                            state.copy(search = search.copy(suggestions = autoCompleteItemList))
                        } else {
                            state
                        }
                    }
                }
            )
        }
    }

    private fun onSearchSubmitted() {
        val autoCompleteItem: AutoCompleteItem = mutableState.value.search?.suggestions?.firstOrNull() ?: return
        onSuggestionClicked(autoCompleteItem = autoCompleteItem)
    }

    private fun onSuggestionClicked(autoCompleteItem: AutoCompleteItem) {
        searchJob?.cancel()
        viewModelScope.launch(context = coroutineDispatcher) {
            setLocationModeTask(value = false)
                .flatMap { setLocationInformationTask(autoCompleteItem = autoCompleteItem) }
                .onResult(
                    { failure: Failure ->
                        displayError(failure = failure)
                    },
                    {
                        mutableState.update { state: WeatherForecastState ->
                            state.copy(
                                search = null,
                                isRefreshing = true
                            )
                        }
                        load(
                            isAutomatic = false,
                            forceNet = true
                        )
                    }
                )
        }
    }

    private fun displayError(failure: Failure) {
        Timber.e("Failure: $failure")
        mutableState.update { state: WeatherForecastState ->
            state.copy(
                isLoading = false,
                isRefreshing = false,
                errorMessageResourceId = getErrorCode(failure)
            )
        }
    }
}