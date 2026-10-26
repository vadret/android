package fi.kroon.vadret.weatherforecast

import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import fi.kroon.vadret.autocomplete.model.AutoCompleteItem
import fi.kroon.vadret.common.Failure
import fi.kroon.vadret.favorite.model.Favorite
import fi.kroon.vadret.ui.extension.launchReplacing
import fi.kroon.vadret.ui.extension.messageResourceId
import fi.kroon.vadret.weatherforecast.extension.toWeatherForecast
import fi.kroon.vadret.weatherforecast.extension.withComingDays
import fi.kroon.vadret.weatherforecast.extension.withDayToggled
import fi.kroon.vadret.weatherforecast.extension.withForecast
import fi.kroon.vadret.weatherforecast.model.LocationChoices
import fi.kroon.vadret.weatherforecast.model.PlaceForecast
import fi.kroon.vadret.weatherforecast.model.SearchState
import fi.kroon.vadret.weatherforecast.model.WeatherForecast
import io.github.sphrak.either.Either
import io.github.sphrak.either.extension.asRight
import io.github.sphrak.either.extension.errorOrNull
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
import kotlin.concurrent.atomics.AtomicBoolean
import kotlin.concurrent.atomics.AtomicReference
import kotlin.time.Clock
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds

private val searchDebounce: Duration = 70.milliseconds

@Immutable
class WeatherForecastViewModel internal constructor(
    private val getLocationModeTask: GetLocationModeTask,
    private val setLocationModeTask: SetLocationModeTask,
    private val getWeatherForecastTask: GetWeatherForecastTask,
    private val observeLocationChoicesTask: ObserveLocationChoicesTask,
    private val selectFavoriteTask: SelectFavoriteTask,
    private val removeFavoriteTask: RemoveFavoriteTask,
    private val searchPlacesTask: SearchPlacesTask,
    private val selectPlaceTask: SelectPlaceTask,
    private val clock: Clock,
    private val coroutineDispatcher: CoroutineDispatcher
) : ViewModel() {

    val state: StateFlow<WeatherForecastState>
        field = MutableStateFlow(value = WeatherForecastState())

    private val loadedLocationMode: AtomicReference<Boolean?> = AtomicReference(value = null)
    private val locale: AtomicReference<Locale> = AtomicReference(value = Locale.getDefault())
    private val placeForecast: AtomicReference<PlaceForecast?> = AtomicReference(value = null)
    private val isObservingLocationChoices: AtomicBoolean = AtomicBoolean(value = false)
    private val loadJob: AtomicReference<Job?> = AtomicReference(value = null)
    private val searchJob: AtomicReference<Job?> = AtomicReference(value = null)

    fun onEvent(event: WeatherForecastEvent) {
        when (event) {
            WeatherForecastEvent.OnShown -> onShown()

            is WeatherForecastEvent.OnLocaleChanged -> onLocaleChanged(locale = event.locale)

            WeatherForecastEvent.OnRefresh -> onRefresh()

            WeatherForecastEvent.OnErrorShown -> state.update { state: WeatherForecastState ->
                state.copy(errorMessageResourceId = null)
            }

            is WeatherForecastEvent.OnForecastViewSelected -> state.update { state: WeatherForecastState ->
                state.copy(forecastView = event.forecastView)
            }

            is WeatherForecastEvent.OnDayToggled -> onDayToggled(dayKey = event.dayKey)

            is WeatherForecastEvent.OnLocationPermissionResult -> onLocationPermissionResult(isGranted = event.isGranted)

            WeatherForecastEvent.OnAutomaticLocationClicked -> state.update { state: WeatherForecastState ->
                state.copy(isLocationPermissionRequested = true)
            }

            is WeatherForecastEvent.OnFavoriteClicked -> onFavoriteClicked(favorite = event.favorite)

            is WeatherForecastEvent.OnFavoriteRemoveClicked -> onFavoriteRemoveClicked(favoriteId = event.favoriteId)

            WeatherForecastEvent.OnAddPlaceClicked -> state.update { state: WeatherForecastState ->
                state.copy(search = SearchState())
            }

            WeatherForecastEvent.OnSearchDismissed -> onSearchDismissed()

            WeatherForecastEvent.OnSearchSubmitted -> onSearchSubmitted()

            is WeatherForecastEvent.OnSearchQueryChanged -> onSearchQueryChanged(query = event.query)

            is WeatherForecastEvent.OnSuggestionClicked -> onSuggestionClicked(autoCompleteItem = event.autoCompleteItem)
        }
    }

    private fun onDayToggled(dayKey: String) {
        viewModelScope.launch(context = coroutineDispatcher) {
            state.update { state: WeatherForecastState ->
                state.withComingDays(comingDays = state.forecast.comingDays.withDayToggled(dayKey = dayKey))
            }
        }
    }

    private fun onShown() {
        if (isObservingLocationChoices.compareAndSet(expectedValue = false, newValue = true)) {
            observeLocationChoices()
        }
        viewModelScope.launch(context = coroutineDispatcher) {
            getLocationModeTask()
                .onResult(
                    onError = { failure: Failure -> displayError(failure = failure) },
                    onSuccess = { isAutomatic: Boolean ->
                        if (isAutomatic != loadedLocationMode.load()) {
                            startLoading(isAutomatic = isAutomatic)
                        }
                    }
                )
        }
    }

    private fun observeLocationChoices() {
        viewModelScope.launch(context = coroutineDispatcher) {
            observeLocationChoicesTask().collect { result: Either<Failure, LocationChoices> ->
                result.onResult(
                    onError = { failure: Failure -> displayError(failure = failure) },
                    onSuccess = { locationChoices: LocationChoices ->
                        state.update { state: WeatherForecastState ->
                            state.copy(locationChoices = locationChoices)
                        }
                    }
                )
            }
        }
    }

    private fun onFavoriteClicked(favorite: Favorite) {
        viewModelScope.launch(context = coroutineDispatcher) {
            selectFavoriteTask(favorite = favorite)
                .onResult(
                    onError = { failure: Failure -> displayError(failure = failure) },
                    onSuccess = { reloadStoredLocation() }
                )
        }
    }

    private fun onFavoriteRemoveClicked(favoriteId: String) {
        viewModelScope.launch(context = coroutineDispatcher) {
            val failure: Failure? = removeFavoriteTask(favoriteId = favoriteId).errorOrNull
            if (failure != null) {
                displayError(failure = failure)
            }
        }
    }

    private fun reloadStoredLocation() {
        state.update { state: WeatherForecastState ->
            state.copy(
                search = null,
                isRefreshing = true
            )
        }
        load(isAutomatic = false)
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
        val isAutomatic: Boolean = loadedLocationMode.load() ?: return
        state.update { state: WeatherForecastState ->
            state.copy(isRefreshing = true)
        }
        load(isAutomatic = isAutomatic)
    }

    private fun load(isAutomatic: Boolean) {
        loadedLocationMode.store(newValue = isAutomatic)
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
        val previousPlaceForecast: PlaceForecast? = this.placeForecast.exchange(newValue = placeForecast)
        val isSamePlace: Boolean = previousPlaceForecast?.placeName == placeForecast.placeName
        state.update { state: WeatherForecastState ->
            state
                .withForecast(
                    forecast = placeForecast.toForecast(),
                    isSamePlace = isSamePlace
                ).copy(
                    localityName = placeForecast.placeName,
                    isLoading = false,
                    isRefreshing = false
                )
        }
    }

    private fun onLocaleChanged(locale: Locale) {
        if (this.locale.exchange(newValue = locale) == locale) {
            return
        }
        viewModelScope.launch(context = coroutineDispatcher) {
            val loadedPlaceForecast: PlaceForecast = placeForecast.load() ?: return@launch
            state.update { state: WeatherForecastState ->
                state.withForecast(
                    forecast = loadedPlaceForecast.toForecast(),
                    isSamePlace = true
                )
            }
        }
    }

    private fun PlaceForecast.toForecast(): WeatherForecast =
        toWeatherForecast(
            now = clock.now(),
            timeZone = TimeZone.currentSystemDefault(),
            locale = locale.load()
        )

    private fun onSearchDismissed() {
        searchJob.exchange(newValue = null)?.cancel()
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
        searchJob.exchange(newValue = null)?.cancel()
        viewModelScope.launch(context = coroutineDispatcher) {
            selectPlaceTask(autoCompleteItem = autoCompleteItem)
                .onResult(
                    onError = { failure: Failure -> displayError(failure = failure) },
                    onSuccess = { reloadStoredLocation() }
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