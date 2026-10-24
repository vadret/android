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
class GetRadarLastCheckedKeyValueTaskTest {

    private val errorHandler: ErrorHandler = ErrorHandler()

    private val timeStamp: Long = 1563292476031L

    private lateinit var testGetRadarLastCheckedKeyValueTask: GetRadarLastCheckedKeyValueTask

    @Mock
    private lateinit var mockRadarLocalKeyValueDataSource: RadarLocalKeyValueDataSource

    @Before
    fun setup() {
        testGetRadarLastCheckedKeyValueTask = GetRadarLastCheckedKeyValueTask(mockRadarLocalKeyValueDataSource)
    }

    @Test
    fun `get last checked value by key successfully`() = runTest {

        doReturn(getEitherLong())
            .`when`(mockRadarLocalKeyValueDataSource)
            .getLong(key = LAST_CHECKED_RADAR_KEY)

        assertTrue(
            testGetRadarLastCheckedKeyValueTask()
                .let {
                    it is Either.Right && it.b == timeStamp
                }
        )
    }

    @Test
    fun `get last checked by key and fail with key not found`() = runTest {
        doReturn(errorHandler.getLocalKeyValueReadError<Long>(key = "LAST_CHECKED_RADAR"))
            .`when`(mockRadarLocalKeyValueDataSource)
            .getLong(key = LAST_CHECKED_RADAR_KEY)

        assertTrue(
            testGetRadarLastCheckedKeyValueTask()
                .let {
                    it is Either.Left &&
                        it.a is Failure.LocalKeyValueReadError &&
                        (it.a as Failure.LocalKeyValueReadError).message ==
                        "error: failed reading property for key 'LAST_CHECKED_RADAR'. It was not recognized."
                }
        )
    }

    private fun getEitherLong(): Either<Failure, Long> =
        timeStamp
            .asRight()
}