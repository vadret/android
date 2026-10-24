package fi.kroon.vadret.radar

import android.graphics.Bitmap
import fi.kroon.vadret.common.ErrorCode
import fi.kroon.vadret.common.Failure
import fi.kroon.vadret.core.R
import fi.kroon.vadret.radar.model.Radar
import fi.kroon.vadret.radar.model.RadarImage
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
    private val radarImageLoader = FakeRadarImageLoader()

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(dispatcher = testDispatcher)
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun viewModel(): RadarViewModel {
        val radarViewModel = RadarViewModel(
            getRadarTask = getRadarTask,
            radarImageLoader = radarImageLoader,
            coroutineDispatcher = testDispatcher
        )
        radarViewModel.onEvent(event = RadarEvent.OnStart)
        return radarViewModel
    }

    @Test
    fun `the newest frame is shown once loaded`() = testScope.runTest {
        val radarViewModel: RadarViewModel = viewModel()
        advanceUntilIdle()

        val state: RadarState = radarViewModel.state.value
        assertEquals(expected = 3, actual = state.frames.size)
        assertEquals(expected = 2, actual = state.index)
        assertSame(expected = radarImageLoader.bitmaps["https://smhi.se/2.png"], actual = state.overlay)
        assertEquals(expected = false, actual = state.isLoading)
    }

    @Test
    fun `a radar that cannot be loaded shows its message`() = testScope.runTest {
        getRadarTask.result = Failure(errorCode = ErrorCode.RADAR_NOT_AVAILABLE).asLeft()

        val radarViewModel: RadarViewModel = viewModel()
        advanceUntilIdle()

        assertEquals(expected = R.string.no_radar_available, actual = radarViewModel.state.value.errorMessageResourceId)
        assertEquals(expected = false, actual = radarViewModel.state.value.isLoading)
        radarViewModel.onEvent(event = RadarEvent.OnErrorShown)
        assertNull(actual = radarViewModel.state.value.errorMessageResourceId)
    }

    @Test
    fun `playing steps through the frames and wraps around`() = testScope.runTest {
        val radarViewModel: RadarViewModel = viewModel()
        advanceUntilIdle()

        radarViewModel.onEvent(event = RadarEvent.OnPlayButtonClicked)
        advanceTimeBy(delayTimeMillis = 256)
        runCurrent()
        val firstIndex: Int = radarViewModel.state.value.index
        advanceTimeBy(delayTimeMillis = 256)
        runCurrent()
        val secondIndex: Int = radarViewModel.state.value.index
        radarViewModel.onEvent(event = RadarEvent.OnPlayButtonClicked)
        advanceUntilIdle()

        assertEquals(expected = listOf(0, 1), actual = listOf(firstIndex, secondIndex))
        assertEquals(expected = false, actual = radarViewModel.state.value.isPlaying)
    }

    @Test
    fun `a frame that fails to load stops playback`() = testScope.runTest {
        radarImageLoader.failingUrls = setOf("https://smhi.se/0.png")
        val radarViewModel: RadarViewModel = viewModel()
        advanceUntilIdle()

        radarViewModel.onEvent(event = RadarEvent.OnPlayButtonClicked)
        advanceUntilIdle()

        assertEquals(expected = false, actual = radarViewModel.state.value.isPlaying)
        assertEquals(expected = R.string.network_failure, actual = radarViewModel.state.value.errorMessageResourceId)
    }

    @Test
    fun `dragging the slider pauses and shows that frame`() = testScope.runTest {
        val radarViewModel: RadarViewModel = viewModel()
        advanceUntilIdle()
        radarViewModel.onEvent(event = RadarEvent.OnPlayButtonClicked)

        radarViewModel.onEvent(event = RadarEvent.OnIndexChanged(index = 1))
        advanceUntilIdle()

        val state: RadarState = radarViewModel.state.value
        assertEquals(expected = false, actual = state.isPlaying)
        assertEquals(expected = 1, actual = state.index)
        assertSame(expected = radarImageLoader.bitmaps["https://smhi.se/1.png"], actual = state.overlay)
    }
}