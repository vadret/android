package fi.kroon.vadret.radar

import fi.kroon.vadret.data.failure.Failure
import fi.kroon.vadret.data.radar.model.File
import fi.kroon.vadret.data.radar.model.Radar
import fi.kroon.vadret.data.radar.model.RadarRequest
import fi.kroon.vadret.exception.TestException
import fi.kroon.vadret.util.extension.asLeft
import fi.kroon.vadret.util.extension.asRight
import io.github.sphrak.either.Either
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.doReturn
import org.mockito.junit.MockitoJUnitRunner

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(MockitoJUnitRunner::class)
class RadarViewModelTest {

    private val initialState = RadarView.State()

    private val TEST_TIME_STAMP = 1563136512344L

    private val testFile: File = File(
        key = "a",
        valid = "b",
        formats = emptyList()
    )

    private val testRadar: Radar = Radar(
        key = "asdf",
        updated = "asdf",
        timeZone = "asdf",
        downloads = emptyList(),
        files = emptyList()
    )

    private val data = GetRadarImageUrlService.Data(
        index = 0,
        maxIndex = 0,
        timeStamp = 0,
        radar = testRadar,
        radarRequest = RadarRequest(),
        file = testFile
    )

    private lateinit var testRadarViewModel: RadarViewModel

    @Mock
    private lateinit var mockGetRadarImageUrlService: GetRadarImageUrlService

    @Mock
    private lateinit var mockGetLastCheckedKeyValueTask: GetRadarLastCheckedKeyValueTask

    @Mock
    private lateinit var mockSetRadarLastCheckedKeyValueTask: SetRadarLastCheckedKeyValueTask

    @Before
    fun setup() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
        testRadarViewModel =
            RadarViewModel(
                state = initialState,
                getRadarImageUrlService = mockGetRadarImageUrlService,
                getRadarLastCheckedKeyValueTask = mockGetLastCheckedKeyValueTask,
                setRadarLastCheckedKeyValueTask = mockSetRadarLastCheckedKeyValueTask
            )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    /**
     *  Sends [event] and returns the first state the view model renders.
     */
    private suspend fun TestScope.renderAfter(event: RadarView.Event): RadarView.State {
        val renderedState = async(start = CoroutineStart.UNDISPATCHED) {
            testRadarViewModel.viewState.first()
        }
        testRadarViewModel.send(event)
        return renderedState.await()
    }

    @Test
    fun `send initial event and receive render event display radar image`() = runTest {

        doReturn(getTimeStampEither())
            .`when`(mockGetLastCheckedKeyValueTask)
            .invoke()

        doReturn(getUnitEither())
            .`when`(mockSetRadarLastCheckedKeyValueTask)
            .invoke(value = TEST_TIME_STAMP)

        doReturn(getResultEither())
            .`when`(mockGetRadarImageUrlService)
            .invoke(timeStamp = TEST_TIME_STAMP, index = 0)

        assertTrue(
            renderAfter(
                RadarView
                    .Event
                    .OnViewInitialised(stateParcel = null)
            )
                .let { receivedState: RadarView.State ->
                    receivedState.renderEvent is RadarView.RenderEvent.DisplayRadarImage
                }
        )
    }

    @Test
    fun `dispatch initial event but fail loading radar imagery and receive render event display error`() = runTest {

        doReturn(getTimeStampEither())
            .`when`(mockGetLastCheckedKeyValueTask)
            .invoke()

        doReturn(getFailureEither())
            .`when`(mockGetRadarImageUrlService)
            .invoke(timeStamp = TEST_TIME_STAMP, index = 0)

        assertTrue(
            renderAfter(
                RadarView
                    .Event
                    .OnViewInitialised(stateParcel = null)
            )
                .let { receivedState: RadarView.State ->
                    receivedState.renderEvent is RadarView.RenderEvent.DisplayError
                }
        )
    }

    @Test
    fun `send OnPlayButtonClicked event and render state SetPlayButtonToPlaying`() = runTest {
        assertTrue(
            renderAfter(
                RadarView
                    .Event
                    .OnPlayButtonClicked
            )
                .let { receivedState: RadarView.State ->
                    receivedState.renderEvent is RadarView.RenderEvent.SetPlayButtonToPlaying
                }
        )
    }

    @Test
    fun `send OnPlayButtonStarted event and render state StartSeekBar`() = runTest {
        assertTrue(
            renderAfter(
                RadarView
                    .Event
                    .OnPlayButtonStarted
            )
                .let { receivedState: RadarView.State ->
                    receivedState.renderEvent is RadarView.RenderEvent.StartSeekBar &&
                        receivedState.isSeekBarRunning
                }
        )
    }

    @Test
    fun `send OnPlayButtonStopped event and render state StopSeekBar`() = runTest {
        assertTrue(
            renderAfter(
                RadarView
                    .Event
                    .OnPlayButtonStopped
            )
                .let { receivedState: RadarView.State ->
                    receivedState.renderEvent is RadarView.RenderEvent.StopSeekBar &&
                        !receivedState.isSeekBarRunning
                }
        )
    }

    @Test
    fun `send OnSeekBarReset event and render state None`() = runTest {
        assertTrue(
            renderAfter(
                RadarView
                    .Event
                    .OnSeekBarReset
            )
                .let { receivedState: RadarView.State ->
                    receivedState.renderEvent is RadarView.RenderEvent.None &&
                        !receivedState.isSeekBarRunning
                }
        )
    }

    @Test
    fun `send OnFailureHandled event and render state SetPlayButtonToStopped`() = runTest {
        assertTrue(
            renderAfter(
                RadarView
                    .Event
                    .OnFailureHandled
            )
                .let { receivedState ->
                    receivedState.renderEvent is RadarView.RenderEvent.SetPlayButtonToStopped &&
                        !receivedState.isSeekBarRunning &&
                        receivedState.currentSeekBarIndex == 0 &&
                        receivedState.seekBarMax == 0
                }
        )
    }

    @Test
    fun `send OnPositionUpdated event and render state ResetSeekBar`() = runTest {
        assertTrue(
            renderAfter(
                RadarView
                    .Event
                    .OnPositionUpdated(position = 0)
            )
                .let { receivedState: RadarView.State ->
                    receivedState.renderEvent is RadarView.RenderEvent.None &&
                        !receivedState.isSeekBarRunning &&
                        receivedState.currentSeekBarIndex == 0 &&
                        receivedState.seekBarMax == 0
                }
        )
    }

    @Test
    fun `send OnPositionUpdated event and render state None`() = runTest {
        assertTrue(
            renderAfter(
                RadarView
                    .Event
                    .OnPositionUpdated(position = 1)
            )
                .let { receivedState: RadarView.State ->
                    receivedState.renderEvent is RadarView.RenderEvent.None &&
                        !receivedState.isSeekBarRunning &&
                        receivedState.currentSeekBarIndex == 1 &&
                        receivedState.seekBarMax == 0
                }
        )
    }

    @Test
    fun `send OnSeekBarStopped event and render state None`() = runTest {
        assertTrue(
            renderAfter(
                RadarView
                    .Event
                    .OnSeekBarStopped
            )
                .let { receivedState: RadarView.State ->
                    receivedState.renderEvent is RadarView.RenderEvent.None &&
                        !receivedState.isSeekBarRunning &&
                        receivedState.currentSeekBarIndex == 0 &&
                        receivedState.seekBarMax == 0
                }
        )
    }

    @Test
    fun `send OnStateParcelUpdated event and render state None`() = runTest {
        assertTrue(
            renderAfter(
                RadarView
                    .Event
                    .OnStateParcelUpdated
            )
                .let { receivedState: RadarView.State ->
                    receivedState.renderEvent is RadarView.RenderEvent.None &&
                        !receivedState.isSeekBarRunning &&
                        receivedState.currentSeekBarIndex == 0 &&
                        receivedState.seekBarMax == 0
                }
        )
    }

    @Test
    fun `send OnSeekBarRestored event and render state DisplayRadarImage`() = runTest {

        doReturn(getTimeStampEither())
            .`when`(mockGetLastCheckedKeyValueTask)
            .invoke()

        doReturn(getUnitEither())
            .`when`(mockSetRadarLastCheckedKeyValueTask)
            .invoke(value = TEST_TIME_STAMP)

        doReturn(getResultEither())
            .`when`(mockGetRadarImageUrlService)
            .invoke(timeStamp = TEST_TIME_STAMP, index = 0)

        assertTrue(
            renderAfter(
                RadarView
                    .Event
                    .OnSeekBarRestored
            )
                .let { receivedState: RadarView.State ->
                    receivedState.renderEvent is RadarView.RenderEvent.DisplayRadarImage &&
                        !receivedState.isSeekBarRunning &&
                        receivedState.currentSeekBarIndex == 0 &&
                        receivedState.seekBarMax == 0
                }
        )
    }

    private fun getUnitEither(): Either<Failure, Unit> =
        Unit.asRight()

    private fun getTimeStampEither(): Either<Failure, Long> =
        TEST_TIME_STAMP
            .asRight()

    private fun getFailureEither(): Either<Failure, GetRadarImageUrlService.Data> =
        TestException
            .Error
            .asLeft()

    private fun getResultEither(): Either<Failure, GetRadarImageUrlService.Data> = data
        .asRight()
}