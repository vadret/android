package fi.kroon.vadret.radar

import fi.kroon.vadret.data.exception.ErrorHandler
import fi.kroon.vadret.data.failure.Failure
import fi.kroon.vadret.data.radar.RadarRepository
import fi.kroon.vadret.data.radar.model.Radar
import fi.kroon.vadret.data.radar.model.RadarRequest
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
class GetRadarImageUrlTaskTest {

    private val errorHandler: ErrorHandler = ErrorHandler()

    private lateinit var testGetRadarImageUrlTask: GetRadarImageUrlTask

    @Mock
    private lateinit var mockRadarRepository: RadarRepository

    @Mock
    private lateinit var mockRadar: Radar

    @Mock
    private lateinit var mockRadarRequest: RadarRequest

    @Before
    fun setup() {
        testGetRadarImageUrlTask = GetRadarImageUrlTask(repo = mockRadarRepository)
    }

    @Test
    fun `fetch radar object successfully`() = runTest {

        doReturn(getResultEither())
            .`when`(mockRadarRepository)
            .invoke(mockRadarRequest)

        assertTrue(
            testGetRadarImageUrlTask(mockRadarRequest)
                .let {
                    it is Either.Right &&
                        it.b == mockRadar
                }
        )
    }

    @Test
    fun `fetch radar object from network fails`() = runTest {

        doReturn(errorHandler.getNetworkOfflineError<Radar>())
            .`when`(mockRadarRepository)
            .invoke(mockRadarRequest)

        assertTrue(
            testGetRadarImageUrlTask(mockRadarRequest)
                .let {
                    it is Either.Left &&
                        it.a is Failure.NetworkOfflineError &&
                        (it.a as Failure.NetworkOfflineError).message == "error: network offline or not available"
                }
        )
    }

    private fun getResultEither(): Either<Failure, Radar> =
        mockRadar
            .asRight()
}