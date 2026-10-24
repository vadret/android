package fi.kroon.vadret.weatherforecast

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import fi.kroon.vadret.autocomplete.model.AutoCompleteItem
import fi.kroon.vadret.common.Failure
import fi.kroon.vadret.ui.extension.messageResourceId
import fi.kroon.vadret.weatherforecast.extension.toWeatherForecast
import fi.kroon.vadret.weatherforecast.model.PlaceForecast
import fi.kroon.vadret.weatherforecast.model.SearchState
import fi.kroon.vadret.weatherforecast.model.WeatherForecast
import io.github.sphrak.either.Either
import io.github.sphrak.either.extension.asRight
import io.github.sphrak.either.extension.onResult
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.datetime.TimeZone
import java.util.Locale
import javax.inject.Inject
import kotlin.time.Clock
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds

/**
 *  Typing waits this long before searching.
 */
private val searchDebounce: Duration = 70.milliseconds

/**
 *  Jobs and [loadedLocationMode] are only touched on the main
 *  thread, the work itself runs on [coroutineDispatcher].
 */
class WeatherForecastViewModel @Inject internal constructor(
    private val getLocationModeTask: GetLocationModeTask,
    private val setLocationModeTask: SetLocationModeTask,
    private val getWeatherForecastTask: GetWeatherForecastTask,
    private val searchPlacesTask: SearchPlacesTask,
    private val selectPlaceTask: SelectPlaceTask,
    private val clock: Clock,
    private val coroutineDispatcher: CoroutineDispatcher
) : ViewModel() {

    private val mutableState: MutableStateFlow<WeatherForecastState> = MutableStateFlow(value = WeatherForecastState())
    val state: StateFlow<WeatherForecastState> = mutableState.asStateFlow()

    /**
     *  The location mode the shown forecast was loaded
     *  with, null before the first load.
     */
    private var loadedLocationMode: Boolean? = null
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

    /**
     *  The location mode may have changed in settings meanwhile.
     */
    private fun onShown() {
        viewModelScope.launch {
            withContext(context = coroutineDispatcher) {
                getLocationModeTask()
            }.onResult(
                onError = { failure: Failure -> displayError(failure = failure) },
                onSuccess = { isAutomatic: Boolean ->
                    if (isAutomatic != loadedLocationMode) {
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
            load(isAutomatic = false)
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
        viewModelScope.launch {
            withContext(context = coroutineDispatcher) {
                setLocationModeTask(isAutomatic = isGranted)
            }.onResult(
                onError = { failure: Failure -> displayError(failure = failure) },
                onSuccess = { load(isAutomatic = isGranted) }
            )
        }
    }

    /**
     *  Nothing to refresh until the first load has started.
     */
    private fun onRefresh() {
        val isAutomatic: Boolean = loadedLocationMode ?: return
        mutableState.update { state: WeatherForecastState ->
            state.copy(isRefreshing = true)
        }
        load(isAutomatic = isAutomatic)
    }

    private fun load(isAutomatic: Boolean) {
        loadedLocationMode = isAutomatic
        loadJob?.cancel()
        loadJob = viewModelScope.launch {
            withContext(context = coroutineDispatcher) {
                getWeatherForecastTask()
                    .onResult(
                        onError = { failure: Failure -> displayError(failure = failure) },
                        onSuccess = { placeForecast: PlaceForecast -> displayForecast(placeForecast = placeForecast) }
                    )
            }
        }
    }

    private fun displayForecast(placeForecast: PlaceForecast) {
        val forecast: WeatherForecast = placeForecast.toWeatherForecast(
            now = clock.now(),
            timeZone = TimeZone.currentSystemDefault(),
            locale = Locale.getDefault()
        )
        mutableState.update { state: WeatherForecastState ->
            state.copy(
                forecast = forecast,
                localityName = placeForecast.placeName,
                isLoading = false,
                isRefreshing = false
            )
        }
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
        searchJob = viewModelScope.launch {
            delay(duration = searchDebounce)
            withContext(context = coroutineDispatcher) {
                search(query = query)
            }.onResult(
                onError = { failure: Failure -> displayError(failure = failure) },
                onSuccess = { suggestions: List<AutoCompleteItem> ->
                    displaySuggestions(
                        query = query,
                        suggestions = suggestions
                    )
                }
            )
        }
    }

    private suspend fun search(query: String): Either<Failure, List<AutoCompleteItem>> =
        if (query.isBlank()) {
            emptyList<AutoCompleteItem>().asRight()
        } else {
            searchPlacesTask(prefix = query)
        }

    /**
     *  Suggestions for a query the user has typed past are dropped.
     */
    private fun displaySuggestions(
        query: String,
        suggestions: List<AutoCompleteItem>
    ) {
        mutableState.update { state: WeatherForecastState ->
            val search: SearchState? = state.search
            if (search != null && search.query == query) {
                state.copy(search = search.copy(suggestions = suggestions))
            } else {
                state
            }
        }
    }

    private fun onSearchSubmitted() {
        val autoCompleteItem: AutoCompleteItem = mutableState.value.search?.suggestions?.firstOrNull() ?: return
        onSuggestionClicked(autoCompleteItem = autoCompleteItem)
    }

    private fun onSuggestionClicked(autoCompleteItem: AutoCompleteItem) {
        searchJob?.cancel()
        viewModelScope.launch {
            withContext(context = coroutineDispatcher) {
                selectPlaceTask(autoCompleteItem = autoCompleteItem)
            }.onResult(
                onError = { failure: Failure -> displayError(failure = failure) },
                onSuccess = {
                    mutableState.update { state: WeatherForecastState ->
                        state.copy(
                            search = null,
                            isRefreshing = true
                        )
                    }
                    load(isAutomatic = false)
                }
            )
        }
    }

    private fun displayError(failure: Failure) {
        mutableState.update { state: WeatherForecastState ->
            state.copy(
                isLoading = false,
                isRefreshing = false,
                errorMessageResourceId = failure.messageResourceId()
            )
        }
    }
}