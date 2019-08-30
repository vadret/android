package fi.kroon.vadret.domain.radar

import fi.kroon.vadret.data.exception.ErrorHandler
import fi.kroon.vadret.data.exception.Failure
import fi.kroon.vadret.data.radar.RadarRepository
import fi.kroon.vadret.data.radar.model.Radar
import fi.kroon.vadret.data.radar.model.RadarRequest
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
    fun `fetch radar object successfully`() {

        doReturn(getResultEither())
            .`when`(mockRadarRepository)
            .invoke(mockRadarRequest)

        testGetRadarImageUrlTask(mockRadarRequest)
            .test()
            .assertNoErrors()
            .assertComplete()
            .assertValueAt(0) {
                it is Either.Right &&
                    it.b == mockRadar
            }
    }

    @Test
    fun `fetch radar object from network fails`() {

        doReturn(errorHandler.getNetworkOfflineError<Radar>())
            .`when`(mockRadarRepository)
            .invoke(mockRadarRequest)

        testGetRadarImageUrlTask(mockRadarRequest)
            .test()
            .assertNoErrors()
            .assertComplete()
            .assertValueAt(0) {
                it is Either.Left &&
                    it.a is Failure.NetworkOfflineError &&
                    (it.a as Failure.NetworkOfflineError).message == "error: network offline or not available"
            }
    }

    private fun getResultEither(): Single<Either<Failure, Radar>> =
        mockRadar
            .asRight()
            .asSingle()
}