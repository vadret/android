package fi.kroon.vadret.weatherforecast

import fi.kroon.vadret.common.ErrorCode
import fi.kroon.vadret.common.Failure
import fi.kroon.vadret.core.R
import fi.kroon.vadret.testing.FixedClock
import fi.kroon.vadret.weatherforecast.model.SearchState
import io.github.sphrak.either.extension.asLeft
import io.github.sphrak.either.extension.asRight
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue
import kotlin.time.Instant

@OptIn(ExperimentalCoroutinesApi::class)
class WeatherForecastViewModelTest {

    private val testDispatcher: TestDispatcher = StandardTestDispatcher()
    private val testScope = TestScope(testDispatcher)
    private val locationRepository = FakeLocationRepository()
    private val weatherForecastRepository = FakeWeatherForecastRepository()
    private val autoCompleteRepository = FakeAutoCompleteRepository()
    private val weatherForecastViewModel = WeatherForecastViewModel(
        getLocationModeTask = DefaultGetLocationModeTask(locationRepository = locationRepository),
        setLocationModeTask = DefaultSetLocationModeTask(locationRepository = locationRepository),
        getWeatherForecastTask = DefaultGetWeatherForecastTask(
            locationRepository = locationRepository,
            weatherForecastRepository = weatherForecastRepository,
            nominatimRepository = FakeNominatimRepository()
        ),
        searchPlacesTask = DefaultSearchPlacesTask(autoCompleteRepository = autoCompleteRepository),
        selectPlaceTask = DefaultSelectPlaceTask(locationRepository = locationRepository),
        clock = FixedClock(now = Instant.parse(input = "2026-10-24T12:00:00Z")),
        coroutineDispatcher = testDispatcher
    )

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(dispatcher = testDispatcher)
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `a stored place is loaded when shown`() = testScope.runTest {
        weatherForecastViewModel.onEvent(event = WeatherForecastEvent.OnShown)
        advanceUntilIdle()

        val state: WeatherForecastState = weatherForecastViewModel.state.value
        assertEquals(expected = "Stockholm", actual = state.localityName)
        assertEquals(expected = false, actual = state.isLoading)
        assertTrue(actual = state.forecast.days.isNotEmpty())
    }

    @Test
    fun `showing again only reloads when the mode changed`() = testScope.runTest {
        weatherForecastViewModel.onEvent(event = WeatherForecastEvent.OnShown)
        advanceUntilIdle()
        weatherForecastViewModel.onEvent(event = WeatherForecastEvent.OnShown)
        advanceUntilIdle()
        assertEquals(expected = 1, actual = weatherForecastRepository.requestedCoordinates.size)

        locationRepository.isAutomatic = true.asRight()
        weatherForecastViewModel.onEvent(event = WeatherForecastEvent.OnShown)
        advanceUntilIdle()

        assertTrue(actual = weatherForecastViewModel.state.value.isLocationPermissionRequested)
        assertEquals(expected = 1, actual = weatherForecastRepository.requestedCoordinates.size)
    }

    @Test
    fun `the permission answer sets the mode and loads`() = testScope.runTest {
        locationRepository.isAutomatic = true.asRight()
        weatherForecastViewModel.onEvent(event = WeatherForecastEvent.OnShown)
        advanceUntilIdle()

        weatherForecastViewModel.onEvent(event = WeatherForecastEvent.OnLocationPermissionResult(isGranted = false))
        advanceUntilIdle()

        assertEquals(expected = false, actual = weatherForecastViewModel.state.value.isLocationPermissionRequested)
        assertEquals(expected = listOf("automatic=false"), actual = locationRepository.writes)
        assertEquals(expected = listOf(59.3293 to 18.0686), actual = weatherForecastRepository.requestedCoordinates)
    }

    @Test
    fun `a refresh before the first load does nothing`() = testScope.runTest {
        weatherForecastViewModel.onEvent(event = WeatherForecastEvent.OnRefresh)
        advanceUntilIdle()

        assertEquals(expected = false, actual = weatherForecastViewModel.state.value.isRefreshing)
        assertTrue(actual = weatherForecastRepository.requestedCoordinates.isEmpty())
    }

    @Test
    fun `a failed load shows its message until seen`() = testScope.runTest {
        weatherForecastRepository.forecasts = Failure(errorCode = ErrorCode.WEATHER_FORECAST_NOT_AVAILABLE_FOR_LOCATION).asLeft()
        weatherForecastViewModel.onEvent(event = WeatherForecastEvent.OnShown)
        advanceUntilIdle()

        assertEquals(expected = R.string.data_not_available_at_this_location, actual = weatherForecastViewModel.state.value.errorMessageResourceId)
        weatherForecastViewModel.onEvent(event = WeatherForecastEvent.OnErrorShown)
        assertNull(actual = weatherForecastViewModel.state.value.errorMessageResourceId)
    }

    @Test
    fun `typing searches once it pauses and keeps only the latest query`() = testScope.runTest {
        weatherForecastViewModel.onEvent(event = WeatherForecastEvent.OnSearchButtonClicked)
        weatherForecastViewModel.onEvent(event = WeatherForecastEvent.OnSearchQueryChanged(query = "K"))
        advanceTimeBy(delayTimeMillis = 30)
        weatherForecastViewModel.onEvent(event = WeatherForecastEvent.OnSearchQueryChanged(query = "Ki"))
        advanceUntilIdle()

        assertEquals(expected = listOf("Ki" to 10), actual = autoCompleteRepository.searches)
        assertEquals(expected = SearchState(query = "Ki", suggestions = persistentListOf(kiruna)), actual = weatherForecastViewModel.state.value.search)
    }

    @Test
    fun `a blank query clears the suggestions without searching`() = testScope.runTest {
        weatherForecastViewModel.onEvent(event = WeatherForecastEvent.OnSearchButtonClicked)
        weatherForecastViewModel.onEvent(event = WeatherForecastEvent.OnSearchQueryChanged(query = " "))
        advanceUntilIdle()

        assertTrue(actual = autoCompleteRepository.searches.isEmpty())
        assertEquals(expected = SearchState(query = " ", suggestions = persistentListOf()), actual = weatherForecastViewModel.state.value.search)
    }

    @Test
    fun `submitting picks the first suggestion and loads its forecast`() = testScope.runTest {
        weatherForecastViewModel.onEvent(event = WeatherForecastEvent.OnSearchButtonClicked)
        weatherForecastViewModel.onEvent(event = WeatherForecastEvent.OnSearchQueryChanged(query = "Kir"))
        advanceUntilIdle()

        weatherForecastViewModel.onEvent(event = WeatherForecastEvent.OnSearchSubmitted)
        advanceUntilIdle()

        val state: WeatherForecastState = weatherForecastViewModel.state.value
        assertNull(actual = state.search)
        assertEquals(expected = "Kiruna", actual = state.localityName)
        assertEquals(expected = listOf(67.8558 to 20.2253), actual = weatherForecastRepository.requestedCoordinates)
    }

    @Test
    fun `dismissing the search closes it`() = testScope.runTest {
        weatherForecastViewModel.onEvent(event = WeatherForecastEvent.OnSearchButtonClicked)
        weatherForecastViewModel.onEvent(event = WeatherForecastEvent.OnSearchDismissed)

        assertNull(actual = weatherForecastViewModel.state.value.search)
    }
}