package fi.kroon.vadret.presentation

import fi.kroon.vadret.data.exception.Either
import fi.kroon.vadret.data.exception.Failure
import fi.kroon.vadret.data.radar.RadarRequest
import fi.kroon.vadret.data.radar.model.Radar
import fi.kroon.vadret.domain.RadarUseCase
import fi.kroon.vadret.presentation.viewmodel.RadarViewModel
import io.reactivex.Single
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.doReturn
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class RadarViewModelTest {

    @Mock
    private lateinit var mockRadarUseCase: RadarUseCase

    @Mock
    private lateinit var mockRadarRequest: RadarRequest

    @Mock
    private lateinit var mockRadar: Radar

    private lateinit var testRadarViewModel: RadarViewModel

    private val testThrowable = Throwable()

    @Before
    fun setup() {
        testRadarViewModel = RadarViewModel(mockRadarUseCase)
    }

    @Test
    fun `usecase returns single radar`() {
        val testRadar = Either.Right(mockRadar) as Either<Failure, Radar>
        val testRadarSingle = Single.just(testRadar)

        doReturn(testRadarSingle).`when`(mockRadarUseCase).get(mockRadarRequest)

        testRadarViewModel
            .get(mockRadarRequest)
            .test()
            .assertResult(testRadar)
    }

    @Test
    fun `usecase returns single failure`() {
        val testFailure = Either.Left(Failure.IOException())
        val testFailureSingle = Single.just(testFailure)
        doReturn(testFailureSingle).`when`(mockRadarUseCase).get(mockRadarRequest)

        testRadarViewModel
            .get(mockRadarRequest)
            .test()
            .assertResult(testFailure)
    }

    @Test
    fun `usecase throws exception`() {
        val testThrowableSingle = Single.error<Either<Failure, Radar>>(testThrowable)
        doReturn(testThrowableSingle).`when`(mockRadarUseCase).get(mockRadarRequest)

        testRadarViewModel
            .get(mockRadarRequest)
            .test()
            .assertValueAt(0) { it is Either.Left<Failure> && it.a is Failure.IOException }
    }
}