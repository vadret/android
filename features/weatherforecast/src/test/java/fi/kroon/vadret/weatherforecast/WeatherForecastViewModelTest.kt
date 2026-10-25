package fi.kroon.vadret.weatherforecast

import fi.kroon.vadret.common.ErrorCode
import fi.kroon.vadret.common.Failure
import fi.kroon.vadret.core.R
import fi.kroon.vadret.testing.FixedClock
import fi.kroon.vadret.weatherforecast.model.FavoriteChoice
import fi.kroon.vadret.weatherforecast.model.ForecastDay
import fi.kroon.vadret.weatherforecast.model.LocationChoices
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
import java.util.Locale
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
    private val favoriteRepository = FakeFavoriteRepository()
    private val weatherForecastViewModel = WeatherForecastViewModel(
        getLocationModeTask = DefaultGetLocationModeTask(locationRepository = locationRepository),
        setLocationModeTask = DefaultSetLocationModeTask(locationRepository = locationRepository),
        getWeatherForecastTask = DefaultGetWeatherForecastTask(
            locationRepository = locationRepository,
            weatherForecastRepository = weatherForecastRepository,
            nominatimRepository = FakeNominatimRepository()
        ),
        observeLocationChoicesTask = DefaultObserveLocationChoicesTask(
            favoriteRepository = favoriteRepository,
            locationRepository = locationRepository
        ),
        selectFavoriteTask = DefaultSelectFavoriteTask(locationRepository = locationRepository),
        removeFavoriteTask = DefaultRemoveFavoriteTask(favoriteRepository = favoriteRepository),
        searchPlacesTask = DefaultSearchPlacesTask(autoCompleteRepository = autoCompleteRepository),
        selectPlaceTask = DefaultSelectPlaceTask(
            favoriteRepository = favoriteRepository,
            locationRepository = locationRepository
        ),
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
    fun `a stored place is loaded when shown`() =
        testScope.runTest {
            weatherForecastViewModel.onEvent(event = WeatherForecastEvent.OnShown)
            advanceUntilIdle()

            val state: WeatherForecastState = weatherForecastViewModel.state.value
            assertEquals(expected = "Stockholm", actual = state.localityName)
            assertEquals(expected = false, actual = state.isLoading)
            assertTrue(actual = state.forecast.days.isNotEmpty())
        }

    @Test
    fun `showing again only reloads when the mode changed`() =
        testScope.runTest {
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
    fun `the permission answer sets the mode and loads`() =
        testScope.runTest {
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
    fun `a refresh before the first load does nothing`() =
        testScope.runTest {
            weatherForecastViewModel.onEvent(event = WeatherForecastEvent.OnRefresh)
            advanceUntilIdle()

            assertEquals(expected = false, actual = weatherForecastViewModel.state.value.isRefreshing)
            assertTrue(actual = weatherForecastRepository.requestedCoordinates.isEmpty())
        }

    @Test
    fun `a failed load shows its message until seen`() =
        testScope.runTest {
            weatherForecastRepository.forecasts = Failure(errorCode = ErrorCode.WEATHER_FORECAST_NOT_AVAILABLE_FOR_LOCATION).asLeft()
            weatherForecastViewModel.onEvent(event = WeatherForecastEvent.OnShown)
            advanceUntilIdle()

            assertEquals(
                expected = R.string.data_not_available_at_this_location,
                actual = weatherForecastViewModel.state.value.errorMessageResourceId
            )
            weatherForecastViewModel.onEvent(event = WeatherForecastEvent.OnErrorShown)
            assertNull(actual = weatherForecastViewModel.state.value.errorMessageResourceId)
        }

    @Test
    fun `typing searches once it pauses and keeps only the latest query`() =
        testScope.runTest {
            weatherForecastViewModel.onEvent(event = WeatherForecastEvent.OnAddPlaceClicked)
            weatherForecastViewModel.onEvent(event = WeatherForecastEvent.OnSearchQueryChanged(query = "K"))
            advanceTimeBy(delayTimeMillis = 30)
            weatherForecastViewModel.onEvent(event = WeatherForecastEvent.OnSearchQueryChanged(query = "Ki"))
            advanceUntilIdle()

            assertEquals(expected = listOf("Ki" to 10), actual = autoCompleteRepository.searches)
            assertEquals(
                expected = SearchState(
                    query = "Ki",
                    suggestions = persistentListOf(kiruna)
                ),
                actual = weatherForecastViewModel.state.value.search
            )
        }

    @Test
    fun `a blank query clears the suggestions without searching`() =
        testScope.runTest {
            weatherForecastViewModel.onEvent(event = WeatherForecastEvent.OnAddPlaceClicked)
            weatherForecastViewModel.onEvent(event = WeatherForecastEvent.OnSearchQueryChanged(query = " "))
            advanceUntilIdle()

            assertTrue(actual = autoCompleteRepository.searches.isEmpty())
            assertEquals(
                expected = SearchState(
                    query = " ",
                    suggestions = persistentListOf()
                ),
                actual = weatherForecastViewModel.state.value.search
            )
        }

    @Test
    fun `submitting picks the first suggestion and loads its forecast`() =
        testScope.runTest {
            weatherForecastViewModel.onEvent(event = WeatherForecastEvent.OnAddPlaceClicked)
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
    fun `dismissing the search closes it`() =
        testScope.runTest {
            weatherForecastViewModel.onEvent(event = WeatherForecastEvent.OnAddPlaceClicked)
            weatherForecastViewModel.onEvent(event = WeatherForecastEvent.OnSearchDismissed)

            assertNull(actual = weatherForecastViewModel.state.value.search)
        }

    @Test
    fun `a picked suggestion becomes the selected favorite`() =
        testScope.runTest {
            weatherForecastViewModel.onEvent(event = WeatherForecastEvent.OnShown)
            advanceUntilIdle()

            weatherForecastViewModel.onEvent(event = WeatherForecastEvent.OnSuggestionClicked(autoCompleteItem = kiruna))
            advanceUntilIdle()

            assertEquals(
                expected = LocationChoices(
                    isAutomatic = false,
                    favorites = persistentListOf(FavoriteChoice(favorite = kirunaFavorite, isSelected = true))
                ),
                actual = weatherForecastViewModel.state.value.locationChoices
            )
        }

    @Test
    fun `a clicked favorite loads its forecast`() =
        testScope.runTest {
            favoriteRepository.favorites.value = listOf(kirunaFavorite)
            weatherForecastViewModel.onEvent(event = WeatherForecastEvent.OnShown)
            advanceUntilIdle()

            weatherForecastViewModel.onEvent(event = WeatherForecastEvent.OnFavoriteClicked(favorite = kirunaFavorite))
            advanceUntilIdle()

            val state: WeatherForecastState = weatherForecastViewModel.state.value
            assertEquals(expected = "Kiruna", actual = state.localityName)
            assertEquals(expected = false, actual = state.isRefreshing)
            assertEquals(expected = 67.8558 to 20.2253, actual = weatherForecastRepository.requestedCoordinates.last())
            assertEquals(
                expected = listOf(true),
                actual = state.locationChoices.favorites.map { favoriteChoice: FavoriteChoice -> favoriteChoice.isSelected }
            )
        }

    @Test
    fun `a removed favorite leaves the choices`() =
        testScope.runTest {
            favoriteRepository.favorites.value = listOf(kirunaFavorite)
            weatherForecastViewModel.onEvent(event = WeatherForecastEvent.OnShown)
            advanceUntilIdle()

            weatherForecastViewModel.onEvent(event = WeatherForecastEvent.OnFavoriteRemoveClicked(favoriteId = kirunaFavorite.favoriteId))
            advanceUntilIdle()

            assertEquals(
                expected = persistentListOf(),
                actual = weatherForecastViewModel.state.value.locationChoices.favorites
            )
        }

    @Test
    fun `a favorite that cannot be removed shows a message`() =
        testScope.runTest {
            weatherForecastViewModel.onEvent(event = WeatherForecastEvent.OnShown)
            advanceUntilIdle()
            favoriteRepository.failure = Failure(errorCode = ErrorCode.STORAGE_FAILED)

            weatherForecastViewModel.onEvent(event = WeatherForecastEvent.OnFavoriteRemoveClicked(favoriteId = kirunaFavorite.favoriteId))
            advanceUntilIdle()

            assertEquals(
                expected = R.string.unhandled_error,
                actual = weatherForecastViewModel.state.value.errorMessageResourceId
            )
        }

    @Test
    fun `choosing automatic asks for the location permission`() =
        testScope.runTest {
            weatherForecastViewModel.onEvent(event = WeatherForecastEvent.OnAutomaticLocationClicked)

            assertTrue(actual = weatherForecastViewModel.state.value.isLocationPermissionRequested)
        }

    @Test
    fun `location choices are observed once however often the screen is shown`() =
        testScope.runTest {
            weatherForecastViewModel.onEvent(event = WeatherForecastEvent.OnShown)
            weatherForecastViewModel.onEvent(event = WeatherForecastEvent.OnShown)
            advanceUntilIdle()

            assertEquals(expected = 1, actual = favoriteRepository.favorites.subscriptionCount.value)
        }

    @Test
    fun `nothing is observed before the screen is shown`() =
        testScope.runTest {
            advanceUntilIdle()

            assertEquals(expected = 0, actual = favoriteRepository.favorites.subscriptionCount.value)
        }

    @Test
    fun `a new language rewrites the day headings of the loaded forecast`() =
        testScope.runTest {
            weatherForecastViewModel.onEvent(event = WeatherForecastEvent.OnLocaleChanged(locale = Locale.ENGLISH))
            weatherForecastViewModel.onEvent(event = WeatherForecastEvent.OnShown)
            advanceUntilIdle()
            val english: ForecastDay = weatherForecastViewModel.state.value.forecast.days.first()

            weatherForecastViewModel.onEvent(event = WeatherForecastEvent.OnLocaleChanged(locale = Locale.forLanguageTag("sv-SE")))
            advanceUntilIdle()
            val swedish: ForecastDay = weatherForecastViewModel.state.value.forecast.days.first()

            assertEquals(expected = "Saturday" to "October 24, 2026", actual = english.weekday to english.dateText)
            assertEquals(expected = "Lördag" to "24 oktober 2026", actual = swedish.weekday to swedish.dateText)
            assertEquals(expected = "Stockholm", actual = weatherForecastViewModel.state.value.localityName)
        }

    @Test
    fun `a new language before any forecast changes nothing`() =
        testScope.runTest {
            val before: WeatherForecastState = weatherForecastViewModel.state.value

            weatherForecastViewModel.onEvent(event = WeatherForecastEvent.OnLocaleChanged(locale = Locale.forLanguageTag("sv-SE")))
            advanceUntilIdle()

            assertEquals(expected = before, actual = weatherForecastViewModel.state.value)
        }
}