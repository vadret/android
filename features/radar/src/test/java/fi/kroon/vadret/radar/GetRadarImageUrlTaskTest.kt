package fi.kroon.vadret.radar

import fi.kroon.vadret.data.exception.ErrorHandler
import fi.kroon.vadret.data.failure.Failure
import fi.kroon.vadret.data.radar.RadarRepository
import fi.kroon.vadret.data.radar.model.Radar
import fi.kroon.vadret.data.radar.model.RadarRequest
import fi.kroon.vadret.util.extension.asRight
import io.github.sphrak.either.Either
import kotlinx.coroutines.test.runTest
import org.mockito.Mockito.doReturn
import org.mockito.Mockito.mock
import kotlin.test.Test
import kotlin.test.assertTrue

class GetRadarImageUrlTaskTest {

    private val errorHandler: ErrorHandler = ErrorHandler()

    private val mockRadarRepository: RadarRepository = mock(RadarRepository::class.java)

    private val mockRadar: Radar = mock(Radar::class.java)

    private val mockRadarRequest: RadarRequest = mock(RadarRequest::class.java)

    private val testGetRadarImageUrlTask: GetRadarImageUrlTask = GetRadarImageUrlTask(repo = mockRadarRepository)

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