package fi.kroon.vadret.weatherforecast

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import fi.kroon.vadret.autocomplete.model.AutoCompleteItem
import fi.kroon.vadret.common.Failure
import fi.kroon.vadret.ui.extension.launchReplacing
import fi.kroon.vadret.ui.extension.messageResourceId
import fi.kroon.vadret.weatherforecast.extension.toWeatherForecast
import fi.kroon.vadret.weatherforecast.model.PlaceForecast
import fi.kroon.vadret.weatherforecast.model.SearchState
import fi.kroon.vadret.weatherforecast.model.WeatherForecast
import io.github.sphrak.either.Either
import io.github.sphrak.either.extension.asRight
import io.github.sphrak.either.extension.onResult
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.TimeZone
import java.util.Locale
import java.util.concurrent.atomic.AtomicReference
import kotlin.time.Clock
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds

private val searchDebounce: Duration = 70.milliseconds

class WeatherForecastViewModel internal constructor(
    private val getLocationModeTask: GetLocationModeTask,
    private val setLocationModeTask: SetLocationModeTask,
    private val getWeatherForecastTask: GetWeatherForecastTask,
    private val searchPlacesTask: SearchPlacesTask,
    private val selectPlaceTask: SelectPlaceTask,
    private val clock: Clock,
    private val coroutineDispatcher: CoroutineDispatcher
) : ViewModel() {

    val state: StateFlow<WeatherForecastState>
        field = MutableStateFlow(value = WeatherForecastState())

    private val loadedLocationMode: AtomicReference<Boolean?> = AtomicReference(null)
    private val loadJob: AtomicReference<Job?> = AtomicReference(null)
    private val searchJob: AtomicReference<Job?> = AtomicReference(null)

    fun onEvent(event: WeatherForecastEvent) {
        when (event) {
            WeatherForecastEvent.OnShown -> onShown()
            WeatherForecastEvent.OnRefresh -> onRefresh()
            WeatherForecastEvent.OnErrorShown -> state.update { state: WeatherForecastState ->
                state.copy(errorMessageResourceId = null)
            }
            is WeatherForecastEvent.OnLocationPermissionResult -> onLocationPermissionResult(isGranted = event.isGranted)
            WeatherForecastEvent.OnSearchButtonClicked -> state.update { state: WeatherForecastState ->
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
            getLocationModeTask()
                .onResult(
                    onError = { failure: Failure -> displayError(failure = failure) },
                    onSuccess = { isAutomatic: Boolean ->
                        if (isAutomatic != loadedLocationMode.get()) {
                            startLoading(isAutomatic = isAutomatic)
                        }
                    }
                )
        }
    }

    private fun startLoading(isAutomatic: Boolean) {
        if (isAutomatic) {
            state.update { state: WeatherForecastState ->
                state.copy(isLocationPermissionRequested = true)
            }
        } else {
            load(isAutomatic = false)
        }
    }

    private fun onLocationPermissionResult(isGranted: Boolean) {
        state.update { state: WeatherForecastState ->
            state.copy(isLocationPermissionRequested = false)
        }
        viewModelScope.launch(context = coroutineDispatcher) {
            setLocationModeTask(isAutomatic = isGranted)
                .onResult(
                    onError = { failure: Failure -> displayError(failure = failure) },
                    onSuccess = { load(isAutomatic = isGranted) }
                )
        }
    }

    private fun onRefresh() {
        val isAutomatic: Boolean = loadedLocationMode.get() ?: return
        state.update { state: WeatherForecastState ->
            state.copy(isRefreshing = true)
        }
        load(isAutomatic = isAutomatic)
    }

    private fun load(isAutomatic: Boolean) {
        loadedLocationMode.set(isAutomatic)
        viewModelScope.launchReplacing(
            currentJob = loadJob,
            context = coroutineDispatcher
        ) {
            getWeatherForecastTask()
                .onResult(
                    onError = { failure: Failure -> displayError(failure = failure) },
                    onSuccess = { placeForecast: PlaceForecast -> displayForecast(placeForecast = placeForecast) }
                )
        }
    }

    private fun displayForecast(placeForecast: PlaceForecast) {
        val forecast: WeatherForecast = placeForecast.toWeatherForecast(
            now = clock.now(),
            timeZone = TimeZone.currentSystemDefault(),
            locale = Locale.getDefault()
        )
        state.update { state: WeatherForecastState ->
            state.copy(
                forecast = forecast,
                localityName = placeForecast.placeName,
                isLoading = false,
                isRefreshing = false
            )
        }
    }

    private fun onSearchDismissed() {
        searchJob.getAndSet(null)?.cancel()
        state.update { state: WeatherForecastState ->
            state.copy(search = null)
        }
    }

    private fun onSearchQueryChanged(query: String) {
        state.update { state: WeatherForecastState ->
            val search: SearchState? = state.search
            if (search != null) {
                state.copy(search = search.copy(query = query))
            } else {
                state
            }
        }
        viewModelScope.launchReplacing(
            currentJob = searchJob,
            context = coroutineDispatcher
        ) {
            delay(duration = searchDebounce)
            search(query = query).onResult(
                onError = { failure: Failure -> displayError(failure = failure) },
                onSuccess = { suggestions: List<AutoCompleteItem> ->
                    displaySuggestions(
                        query = query,
                        suggestions = suggestions.toImmutableList()
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

    private fun displaySuggestions(
        query: String,
        suggestions: ImmutableList<AutoCompleteItem>
    ) {
        state.update { state: WeatherForecastState ->
            val search: SearchState? = state.search
            if (search != null && search.query == query) {
                state.copy(search = search.copy(suggestions = suggestions))
            } else {
                state
            }
        }
    }

    private fun onSearchSubmitted() {
        val autoCompleteItem: AutoCompleteItem = state.value.search?.suggestions?.firstOrNull() ?: return
        onSuggestionClicked(autoCompleteItem = autoCompleteItem)
    }

    private fun onSuggestionClicked(autoCompleteItem: AutoCompleteItem) {
        searchJob.getAndSet(null)?.cancel()
        viewModelScope.launch(context = coroutineDispatcher) {
            selectPlaceTask(autoCompleteItem = autoCompleteItem)
                .onResult(
                    onError = { failure: Failure -> displayError(failure = failure) },
                    onSuccess = {
                        state.update { state: WeatherForecastState ->
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
        state.update { state: WeatherForecastState ->
            state.copy(
                isLoading = false,
                isRefreshing = false,
                errorMessageResourceId = failure.messageResourceId()
            )
        }
    }
}