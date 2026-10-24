package fi.kroon.vadret.radar

import fi.kroon.vadret.data.exception.ErrorHandler
import fi.kroon.vadret.data.failure.Failure
import fi.kroon.vadret.data.radar.local.RadarLocalKeyValueDataSource
import fi.kroon.vadret.util.LAST_CHECKED_RADAR_KEY
import fi.kroon.vadret.util.extension.asRight
import io.github.sphrak.either.Either
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.doReturn
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class SetRadarLastCheckedKeyValueTaskTest {

    private val errorHandler: ErrorHandler = ErrorHandler()

    private val timeStamp: Long = 1563292476031L

    private lateinit var testSetRadarLastCheckedKeyValueTask: SetRadarLastCheckedKeyValueTask

    @Mock
    private lateinit var mockRadarLocalKeyValueDataSource: RadarLocalKeyValueDataSource

    @Before
    fun setup() {
        testSetRadarLastCheckedKeyValueTask = SetRadarLastCheckedKeyValueTask(mockRadarLocalKeyValueDataSource)
    }

    @Test
    fun `set radar last checked successfully and retrieve unit as result`() = runTest {
        doReturn(getEitherUnit())
            .`when`(mockRadarLocalKeyValueDataSource)
            .putLong(key = LAST_CHECKED_RADAR_KEY, value = timeStamp)

        assertTrue(
            testSetRadarLastCheckedKeyValueTask(value = timeStamp)
                .let {
                    it is Either.Right && it.b == Unit
                }
        )
    }

    @Test
    fun `set radar last checked and fail with local key value error`() = runTest {

        doReturn(
            errorHandler.getLocalKeyValueWriteError<String, Long>(
                key = LAST_CHECKED_RADAR_KEY,
                value = timeStamp
            )
        ).`when`(mockRadarLocalKeyValueDataSource)
            .putLong(key = LAST_CHECKED_RADAR_KEY, value = timeStamp)

        assertTrue(
            testSetRadarLastCheckedKeyValueTask(value = timeStamp)
                .let {
                    it is Either.Left &&
                        it.a is Failure.LocalKeyValueWriteError &&
                        (it.a as Failure.LocalKeyValueWriteError).message ==
                        "error: failed writing '1563292476031'. key was not recognized: 'LAST_CHECKED_RADAR'"
                }
        )
    }

    private fun getEitherUnit(): Either<Failure, Unit> =
        Unit
            .asRight()
}