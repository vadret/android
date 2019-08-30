package fi.kroon.vadret.presentation.radar

import fi.kroon.vadret.data.exception.Failure
import fi.kroon.vadret.data.radar.model.File
import fi.kroon.vadret.data.radar.model.Radar
import fi.kroon.vadret.data.radar.model.RadarRequest
import fi.kroon.vadret.domain.radar.GetRadarImageUrlService
import fi.kroon.vadret.domain.radar.GetRadarLastCheckedKeyValueTask
import fi.kroon.vadret.domain.radar.SetRadarLastCheckedKeyValueTask
import fi.kroon.vadret.exception.TestException
import fi.kroon.vadret.util.extension.asLeft
import fi.kroon.vadret.util.extension.asObservable
import fi.kroon.vadret.util.extension.asRight
import fi.kroon.vadret.util.extension.asSingle
import io.github.sphrak.either.Either
import io.reactivex.Single
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.doReturn
import org.mockito.junit.MockitoJUnitRunner

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
        testRadarViewModel =
            RadarViewModel(
                state = initialState,
                getRadarImageUrlService = mockGetRadarImageUrlService,
                getRadarLastCheckedKeyValueTask = mockGetLastCheckedKeyValueTask,
                setRadarLastCheckedKeyValueTask = mockSetRadarLastCheckedKeyValueTask
            )
    }

    @Test
    fun `send initial event and receive render event display radar image`() {

        doReturn(getTimeStampEither())
            .`when`(mockGetLastCheckedKeyValueTask)
            .invoke()

        doReturn(getUnitEither())
            .`when`(mockSetRadarLastCheckedKeyValueTask)
            .invoke(value = TEST_TIME_STAMP)

        doReturn(getResultEither())
            .`when`(mockGetRadarImageUrlService)
            .invoke(timeStamp = TEST_TIME_STAMP, index = 0)

        RadarView
            .Event
            .OnViewInitialised(stateParcel = null)
            .asObservable()
            .compose(testRadarViewModel())
            .test()
            .assertNoErrors()
            .assertComplete()
            .assertValueAt(0) { receivedState: RadarView.State ->
                receivedState.renderEvent is RadarView.RenderEvent.DisplayRadarImage
            }
    }

    @Test
    fun `dispatch initial event but fail loading radar imagery and receive render event display error`() {

        doReturn(getTimeStampEither())
            .`when`(mockGetLastCheckedKeyValueTask)
            .invoke()

        doReturn(getFailureEither())
            .`when`(mockGetRadarImageUrlService)
            .invoke(timeStamp = TEST_TIME_STAMP, index = 0)

        RadarView
            .Event
            .OnViewInitialised(stateParcel = null)
            .asObservable()
            .compose(testRadarViewModel())
            .test()
            .assertNoErrors()
            .assertComplete()
            .assertValueAt(0) { receivedState: RadarView.State ->
                receivedState.renderEvent is RadarView.RenderEvent.DisplayError
            }
    }

    @Test
    fun `send OnPlayButtonClicked event and render state SetPlayButtonToPlaying`() {
        RadarView
            .Event
            .OnPlayButtonClicked
            .asObservable()
            .compose(testRadarViewModel())
            .test()
            .assertNoErrors()
            .assertComplete()
            .assertValueAt(0) { receivedState: RadarView.State ->
                receivedState.renderEvent is RadarView.RenderEvent.SetPlayButtonToPlaying
            }
    }

    @Test
    fun `send OnPlayButtonStarted event and render state StartSeekBar`() {
        RadarView
            .Event
            .OnPlayButtonStarted
            .asObservable()
            .compose(testRadarViewModel())
            .test()
            .assertNoErrors()
            .assertComplete()
            .assertValueAt(0) { receivedState: RadarView.State ->
                receivedState.renderEvent is RadarView.RenderEvent.StartSeekBar &&
                    receivedState.isSeekBarRunning
            }
    }

    @Test
    fun `send OnPlayButtonStopped event and render state StopSeekBar`() {
        RadarView
            .Event
            .OnPlayButtonStopped
            .asObservable()
            .compose(testRadarViewModel())
            .test()
            .assertNoErrors()
            .assertComplete()
            .assertValueAt(0) { receivedState: RadarView.State ->
                receivedState.renderEvent is RadarView.RenderEvent.StopSeekBar &&
                    !receivedState.isSeekBarRunning
            }
    }

    @Test
    fun `send OnSeekBarReset event and render state None`() {
        RadarView
            .Event
            .OnSeekBarReset
            .asObservable()
            .compose(testRadarViewModel())
            .test()
            .assertNoErrors()
            .assertComplete()
            .assertValueAt(0) { receivedState: RadarView.State ->
                receivedState.renderEvent is RadarView.RenderEvent.None &&
                    !receivedState.isSeekBarRunning
            }
    }

    @Test
    fun `send OnFailureHandled event and render state SetPlayButtonToStopped`() {
        RadarView
            .Event
            .OnFailureHandled
            .asObservable()
            .compose(testRadarViewModel())
            .test()
            .assertNoErrors()
            .assertComplete()
            .assertValueAt(0) { receivedState ->
                receivedState.renderEvent is RadarView.RenderEvent.SetPlayButtonToStopped &&
                    !receivedState.isSeekBarRunning &&
                    receivedState.currentSeekBarIndex == 0 &&
                    receivedState.seekBarMax == 0
            }
    }

    @Test
    fun `send OnPositionUpdated event and render state ResetSeekBar`() {
        RadarView
            .Event
            .OnPositionUpdated(position = 0)
            .asObservable()
            .compose(testRadarViewModel())
            .test()
            .assertNoErrors()
            .assertComplete()
            .assertValueAt(0) { receivedState: RadarView.State ->
                receivedState.renderEvent is RadarView.RenderEvent.None &&
                    !receivedState.isSeekBarRunning &&
                    receivedState.currentSeekBarIndex == 0 &&
                    receivedState.seekBarMax == 0
            }
    }

    @Test
    fun `send OnPositionUpdated event and render state None`() {
        RadarView
            .Event
            .OnPositionUpdated(position = 1)
            .asObservable()
            .compose(testRadarViewModel())
            .test()
            .assertNoErrors()
            .assertComplete()
            .assertValueAt(0) { receivedState: RadarView.State ->
                receivedState.renderEvent is RadarView.RenderEvent.None &&
                    !receivedState.isSeekBarRunning &&
                    receivedState.currentSeekBarIndex == 1 &&
                    receivedState.seekBarMax == 0
            }
    }

    @Test
    fun `send OnSeekBarStopped event and render state None`() {
        RadarView
            .Event
            .OnSeekBarStopped
            .asObservable()
            .compose(testRadarViewModel())
            .test()
            .assertNoErrors()
            .assertComplete()
            .assertValueAt(0) { receivedState: RadarView.State ->
                receivedState.renderEvent is RadarView.RenderEvent.None &&
                    !receivedState.isSeekBarRunning &&
                    receivedState.currentSeekBarIndex == 0 &&
                    receivedState.seekBarMax == 0
            }
    }

    @Test
    fun `send OnStateParcelUpdated event and render state None`() {
        RadarView
            .Event
            .OnStateParcelUpdated
            .asObservable()
            .compose(testRadarViewModel())
            .test()
            .assertNoErrors()
            .assertComplete()
            .assertValueAt(0) { receivedState: RadarView.State ->
                receivedState.renderEvent is RadarView.RenderEvent.None &&
                    !receivedState.isSeekBarRunning &&
                    receivedState.currentSeekBarIndex == 0 &&
                    receivedState.seekBarMax == 0
            }
    }

    @Test
    fun `send OnSeekBarRestored event and render state DisplayRadarImage`() {

        doReturn(getTimeStampEither())
            .`when`(mockGetLastCheckedKeyValueTask)
            .invoke()

        doReturn(getUnitEither())
            .`when`(mockSetRadarLastCheckedKeyValueTask)
            .invoke(value = TEST_TIME_STAMP)

        doReturn(getResultEither())
            .`when`(mockGetRadarImageUrlService)
            .invoke(timeStamp = TEST_TIME_STAMP, index = 0)

        RadarView
            .Event
            .OnSeekBarRestored
            .asObservable()
            .compose(testRadarViewModel())
            .test()
            .assertNoErrors()
            .assertComplete()
            .assertValueAt(0) { receivedState: RadarView.State ->
                receivedState.renderEvent is RadarView.RenderEvent.DisplayRadarImage &&
                    !receivedState.isSeekBarRunning &&
                    receivedState.currentSeekBarIndex == 0 &&
                    receivedState.seekBarMax == 0
            }
    }

    private fun getUnitEither(): Single<Either<Failure, Unit>> =
        Unit.asRight()
            .asSingle()

    private fun getTimeStampEither(): Single<Either<Failure, Long>> =
        TEST_TIME_STAMP
            .asRight()
            .asSingle()

    private fun getFailureEither(): Single<Either<Failure, GetRadarImageUrlService.Data>> =
        TestException
            .Error
            .asLeft()
            .asSingle()

    private fun getResultEither(): Single<Either<Failure, GetRadarImageUrlService.Data>> = data
        .asRight()
        .asSingle()
}