package fi.kroon.vadret.radar

import fi.kroon.vadret.data.exception.ErrorHandler
import fi.kroon.vadret.data.failure.Failure
import fi.kroon.vadret.data.radar.local.RadarLocalKeyValueDataSource
import fi.kroon.vadret.util.LAST_CHECKED_RADAR_KEY
import fi.kroon.vadret.util.extension.asRight
import io.github.sphrak.either.Either
import kotlinx.coroutines.test.runTest
import org.mockito.Mockito.doReturn
import org.mockito.Mockito.mock
import kotlin.test.Test
import kotlin.test.assertTrue

class SetRadarLastCheckedKeyValueTaskTest {

    private val errorHandler: ErrorHandler = ErrorHandler()

    private val timeStamp: Long = 1563292476031L

    private val mockRadarLocalKeyValueDataSource: RadarLocalKeyValueDataSource = mock(RadarLocalKeyValueDataSource::class.java)

    private val testSetRadarLastCheckedKeyValueTask: SetRadarLastCheckedKeyValueTask = SetRadarLastCheckedKeyValueTask(mockRadarLocalKeyValueDataSource)

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