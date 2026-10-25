package fi.kroon.vadret.radar

import android.graphics.Bitmap
import fi.kroon.vadret.common.ErrorCode
import fi.kroon.vadret.common.Failure
import fi.kroon.vadret.core.R
import fi.kroon.vadret.location.model.Location
import fi.kroon.vadret.radar.model.Radar
import fi.kroon.vadret.radar.model.RadarImage
import fi.kroon.vadret.radar.model.RadarLayer
import fi.kroon.vadret.radar.wind.WindField
import fi.kroon.vadret.radar.wind.WindFieldRenderer
import fi.kroon.vadret.radar.wind.WindFieldValues
import fi.kroon.vadret.testing.FixedClock
import fi.kroon.vadret.wind.model.WindForecast
import fi.kroon.vadret.wind.model.WindFrame
import fi.kroon.vadret.wind.model.WindGrid
import io.github.sphrak.either.Either
import io.github.sphrak.either.extension.asLeft
import io.github.sphrak.either.extension.asRight
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertSame
import kotlin.time.Instant

private class FakeGetRadarTask(
    var result: Either<Failure, Radar>
) : GetRadarTask {
    override suspend fun invoke(): Either<Failure, Radar> = result
}

private class FakeRadarImageLoader : RadarImageLoader {

    val loadedUrls = mutableListOf<String>()
    val bitmaps = mutableMapOf<String, Bitmap>()
    var failingUrls: Set<String> = emptySet()

    override suspend fun load(imageUrl: String): Either<Failure, Bitmap> {
        loadedUrls.add(imageUrl)
        return if (imageUrl in failingUrls) {
            Failure(errorCode = ErrorCode.NETWORK_FAILURE).asLeft()
        } else {
            bitmaps.getOrPut(imageUrl) { mockk() }.asRight()
        }
    }
}

private class FakeGetWindForecastTask(
    var result: Either<Failure, WindForecast>
) : GetWindForecastTask {

    var requestCount: Int = 0

    override suspend fun invoke(): Either<Failure, WindForecast> {
        requestCount++
        return result
    }
}

private class FakeGetDeviceLocationTask(
    var result: Either<Failure, Location>
) : GetDeviceLocationTask {
    override suspend fun invoke(): Either<Failure, Location> = result
}

private class FakeWindFieldRenderer : WindFieldRenderer {

    val renderedFrames = mutableListOf<WindFrame>()

    override suspend fun render(
        windGrid: WindGrid,
        windFrame: WindFrame
    ): WindField {
        renderedFrames.add(windFrame)
        return WindField(
            values = WindFieldValues(
                width = 1,
                height = 1,
                eastward = floatArrayOf(0f),
                northward = floatArrayOf(1f),
                speeds = floatArrayOf(1f),
                colors = intArrayOf(0)
            ),
            bitmap = mockk()
        )
    }
}

private val windForecast = WindForecast(
    referenceTime = Instant.parse(input = "2026-10-24T09:30:00Z"),
    grid = WindGrid(
        columnCount = 1,
        rowCount = 1,
        longitudes = listOf(18.0),
        latitudes = listOf(59.3)
    ),
    frames = (10..12).map { hour: Int ->
        WindFrame(
            validAt = Instant.parse(input = "2026-10-24T$hour:00:00Z"),
            windSpeeds = listOf(hour.toFloat()),
            windFromDirections = listOf(270f)
        )
    }
)

private val radar = Radar(
    images = (0..2).map { minute: Int ->
        RadarImage(
            imageUrl = "https://smhi.se/$minute.png",
            validAt = Instant.parse(input = "2026-10-24T12:0$minute:00Z")
        )
    }
)

@OptIn(ExperimentalCoroutinesApi::class)
class RadarViewModelTest {

    private val testDispatcher: TestDispatcher = StandardTestDispatcher()
    private val testScope = TestScope(testDispatcher)
    private val getRadarTask = FakeGetRadarTask(result = radar.asRight())
    private val getWindForecastTask = FakeGetWindForecastTask(result = windForecast.asRight())
    private val getDeviceLocationTask = FakeGetDeviceLocationTask(result = Location(latitude = 59.3, longitude = 18.0).asRight())
    private val radarImageLoader = FakeRadarImageLoader()
    private val windFieldRenderer = FakeWindFieldRenderer()

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(dispatcher = testDispatcher)
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun viewModel(isStarted: Boolean = true): RadarViewModel {
        val radarViewModel = RadarViewModel(
            getRadarTask = getRadarTask,
            getWindForecastTask = getWindForecastTask,
            getDeviceLocationTask = getDeviceLocationTask,
            radarImageLoader = radarImageLoader,
            windFieldRenderer = windFieldRenderer,
            clock = FixedClock(now = Instant.parse(input = "2026-10-24T12:45:00Z")),
            coroutineDispatcher = testDispatcher
        )
        if (isStarted) {
            radarViewModel.onEvent(event = RadarEvent.OnStart)
        }
        return radarViewModel
    }

    @Test
    fun `the newest frame is shown once loaded`() =
        testScope.runTest {
            val radarViewModel: RadarViewModel = viewModel()
            advanceUntilIdle()

            val state: RadarState = radarViewModel.state.value
            assertEquals(expected = 3, actual = state.precipitationFrames.size)
            assertEquals(expected = 2, actual = state.precipitationIndex)
            assertSame(expected = radarImageLoader.bitmaps["https://smhi.se/2.png"], actual = state.precipitationOverlay)
            assertEquals(expected = false, actual = state.isLoading)
        }

    @Test
    fun `a radar that cannot be loaded shows its message`() =
        testScope.runTest {
            getRadarTask.result = Failure(errorCode = ErrorCode.RADAR_NOT_AVAILABLE).asLeft()

            val radarViewModel: RadarViewModel = viewModel()
            advanceUntilIdle()

            assertEquals(expected = R.string.no_radar_available, actual = radarViewModel.state.value.errorMessageResourceId)
            assertEquals(expected = false, actual = radarViewModel.state.value.isLoading)
            radarViewModel.onEvent(event = RadarEvent.OnErrorShown)
            assertNull(actual = radarViewModel.state.value.errorMessageResourceId)
        }

    @Test
    fun `playing steps through the frames and wraps around`() =
        testScope.runTest {
            val radarViewModel: RadarViewModel = viewModel()
            advanceUntilIdle()

            radarViewModel.onEvent(event = RadarEvent.OnPlayButtonClicked)
            advanceTimeBy(delayTimeMillis = 256)
            runCurrent()
            val firstIndex: Int = radarViewModel.state.value.precipitationIndex
            advanceTimeBy(delayTimeMillis = 256)
            runCurrent()
            val secondIndex: Int = radarViewModel.state.value.precipitationIndex
            radarViewModel.onEvent(event = RadarEvent.OnPlayButtonClicked)
            advanceUntilIdle()

            assertEquals(expected = listOf(0, 1), actual = listOf(firstIndex, secondIndex))
            assertEquals(expected = false, actual = radarViewModel.state.value.isPlaying)
        }

    @Test
    fun `a frame that fails to load stops playback`() =
        testScope.runTest {
            radarImageLoader.failingUrls = setOf("https://smhi.se/0.png")
            val radarViewModel: RadarViewModel = viewModel()
            advanceUntilIdle()

            radarViewModel.onEvent(event = RadarEvent.OnPlayButtonClicked)
            advanceUntilIdle()

            assertEquals(expected = false, actual = radarViewModel.state.value.isPlaying)
            assertEquals(expected = R.string.network_failure, actual = radarViewModel.state.value.errorMessageResourceId)
        }

    @Test
    fun `dragging the slider pauses and shows that frame`() =
        testScope.runTest {
            val radarViewModel: RadarViewModel = viewModel()
            advanceUntilIdle()
            radarViewModel.onEvent(event = RadarEvent.OnPlayButtonClicked)

            radarViewModel.onEvent(event = RadarEvent.OnIndexChanged(index = 1))
            advanceUntilIdle()

            val state: RadarState = radarViewModel.state.value
            assertEquals(expected = false, actual = state.isPlaying)
            assertEquals(expected = 1, actual = state.precipitationIndex)
            assertSame(expected = radarImageLoader.bitmaps["https://smhi.se/1.png"], actual = state.precipitationOverlay)
        }

    @Test
    fun `nothing loads before the screen starts the view model`() =
        testScope.runTest {
            viewModel(isStarted = false)
            advanceUntilIdle()

            assertEquals(expected = emptyList(), actual = radarImageLoader.loadedUrls)
            assertEquals(expected = 0, actual = getWindForecastTask.requestCount)
        }

    @Test
    fun `the device location is shown when known`() =
        testScope.runTest {
            val radarViewModel: RadarViewModel = viewModel()
            advanceUntilIdle()

            assertEquals(expected = Location(latitude = 59.3, longitude = 18.0), actual = radarViewModel.state.value.deviceLocation)
        }

    @Test
    fun `an unknown device location shows no marker and no message`() =
        testScope.runTest {
            getDeviceLocationTask.result = Failure(errorCode = ErrorCode.LOCATION_PERMISSION_MISSING).asLeft()

            val radarViewModel: RadarViewModel = viewModel()
            advanceUntilIdle()

            assertNull(actual = radarViewModel.state.value.deviceLocation)
            assertNull(actual = radarViewModel.state.value.errorMessageResourceId)
        }

    @Test
    fun `choosing wind loads the forecast once and shows the first hour`() =
        testScope.runTest {
            val radarViewModel: RadarViewModel = viewModel()
            advanceUntilIdle()

            radarViewModel.onEvent(event = RadarEvent.OnLayerSelected(layer = RadarLayer.WIND))
            advanceUntilIdle()
            radarViewModel.onEvent(event = RadarEvent.OnLayerSelected(layer = RadarLayer.PRECIPITATION))
            radarViewModel.onEvent(event = RadarEvent.OnLayerSelected(layer = RadarLayer.WIND))
            advanceUntilIdle()

            val state: RadarState = radarViewModel.state.value
            assertEquals(expected = RadarLayer.WIND, actual = state.layer)
            assertEquals(expected = 1, actual = getWindForecastTask.requestCount)
            assertEquals(expected = 3, actual = state.windFrames.size)
            assertEquals(expected = 0, actual = state.windIndex)
            assertEquals(expected = windForecast.frames.first(), actual = windFieldRenderer.renderedFrames.first())
            assertEquals(expected = false, actual = state.isLoading)
        }

    @Test
    fun `a wind forecast that fails shows its message and is asked again next time`() =
        testScope.runTest {
            getWindForecastTask.result = Failure(errorCode = ErrorCode.WIND_NOT_AVAILABLE).asLeft()
            val radarViewModel: RadarViewModel = viewModel()
            advanceUntilIdle()

            radarViewModel.onEvent(event = RadarEvent.OnLayerSelected(layer = RadarLayer.WIND))
            advanceUntilIdle()
            assertEquals(expected = R.string.no_wind_available, actual = radarViewModel.state.value.errorMessageResourceId)
            assertEquals(expected = false, actual = radarViewModel.state.value.isLoading)

            radarViewModel.onEvent(event = RadarEvent.OnLayerSelected(layer = RadarLayer.PRECIPITATION))
            radarViewModel.onEvent(event = RadarEvent.OnLayerSelected(layer = RadarLayer.WIND))
            advanceUntilIdle()
            assertEquals(expected = 2, actual = getWindForecastTask.requestCount)
        }

    @Test
    fun `the slider on the wind layer renders that hour`() =
        testScope.runTest {
            val radarViewModel: RadarViewModel = viewModel()
            advanceUntilIdle()
            radarViewModel.onEvent(event = RadarEvent.OnLayerSelected(layer = RadarLayer.WIND))
            advanceUntilIdle()

            radarViewModel.onEvent(event = RadarEvent.OnIndexChanged(index = 2))
            advanceUntilIdle()

            assertEquals(expected = 2, actual = radarViewModel.state.value.windIndex)
            assertEquals(expected = windForecast.frames[2], actual = windFieldRenderer.renderedFrames.last())
            assertEquals(expected = 2, actual = radarViewModel.state.value.precipitationIndex)
        }

    @Test
    fun `playing the wind steps hour by hour`() =
        testScope.runTest {
            val radarViewModel: RadarViewModel = viewModel()
            advanceUntilIdle()
            radarViewModel.onEvent(event = RadarEvent.OnLayerSelected(layer = RadarLayer.WIND))
            advanceUntilIdle()

            radarViewModel.onEvent(event = RadarEvent.OnPlayButtonClicked)
            advanceTimeBy(delayTimeMillis = 600)
            runCurrent()
            val windIndex: Int = radarViewModel.state.value.windIndex
            radarViewModel.onEvent(event = RadarEvent.OnPlayButtonClicked)
            advanceUntilIdle()

            assertEquals(expected = 1, actual = windIndex)
        }

    @Test
    fun `switching layer stops playing`() =
        testScope.runTest {
            val radarViewModel: RadarViewModel = viewModel()
            advanceUntilIdle()
            radarViewModel.onEvent(event = RadarEvent.OnPlayButtonClicked)

            radarViewModel.onEvent(event = RadarEvent.OnLayerSelected(layer = RadarLayer.WIND))
            advanceUntilIdle()

            assertEquals(expected = false, actual = radarViewModel.state.value.isPlaying)
        }

    @Test
    fun `coming back to precipitation keeps the chosen frame`() =
        testScope.runTest {
            val radarViewModel: RadarViewModel = viewModel()
            advanceUntilIdle()
            radarViewModel.onEvent(event = RadarEvent.OnIndexChanged(index = 0))
            advanceUntilIdle()

            radarViewModel.onEvent(event = RadarEvent.OnLayerSelected(layer = RadarLayer.WIND))
            advanceUntilIdle()
            radarViewModel.onEvent(event = RadarEvent.OnLayerSelected(layer = RadarLayer.PRECIPITATION))
            advanceUntilIdle()

            assertEquals(expected = 0, actual = radarViewModel.state.value.precipitationIndex)
        }
}