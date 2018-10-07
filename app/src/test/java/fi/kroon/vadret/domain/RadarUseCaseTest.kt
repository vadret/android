package fi.kroon.vadret.domain

import fi.kroon.vadret.data.exception.Either
import fi.kroon.vadret.data.exception.Failure
import fi.kroon.vadret.data.radar.RadarRepository
import fi.kroon.vadret.data.radar.RadarRequest
import fi.kroon.vadret.data.radar.model.Radar
import io.reactivex.Single
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.doReturn
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class RadarUseCaseTest {

    @Mock
    private lateinit var mockRadarRepository: RadarRepository

    @Mock
    private lateinit var mockRadarRequest: RadarRequest

    @Mock
    private lateinit var mockRadar: Radar

    private lateinit var testRadarUseCase: RadarUseCase

    private val testThrowable = Throwable()

    @Before
    fun setup() {
        testRadarUseCase = RadarUseCase(mockRadarRepository)
    }

    @Test
    fun `repository successfully returns a single`() {
        val testRadar = Either.Right(mockRadar) as Either<Failure, Radar>
        val testSingle = Single.just(testRadar)

        doReturn(testSingle).`when`(mockRadarRepository).get(mockRadarRequest)

        testRadarUseCase
            .get(mockRadarRequest)
            .test()
            .assertResult(testRadar)
    }

    @Test
    fun `repository fails and returns network offline failure`() {
        val testFailure = Either.Left(Failure.NetworkOfflineFailure())
        val testFailureSingle = Single.just(testFailure)
        doReturn(testFailureSingle).`when`(mockRadarRepository).get(mockRadarRequest)

        testRadarUseCase
            .get(mockRadarRequest)
            .test()
            .assertResult(testFailure)
    }

    @Test
    fun `repository fails and returns throwable`() {
        val testThrowableSingle = Single.error<Either<Failure, Radar>>(testThrowable)
        doReturn(testThrowableSingle).`when`(mockRadarRepository).get(mockRadarRequest)

        testRadarUseCase
            .get(mockRadarRequest)
            .test()
            .assertError(Throwable::class.java)
    }
}