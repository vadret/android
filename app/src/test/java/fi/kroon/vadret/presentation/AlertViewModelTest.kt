package fi.kroon.vadret.presentation

import fi.kroon.vadret.data.alert.model.Alert
import fi.kroon.vadret.data.exception.Either
import fi.kroon.vadret.data.exception.Failure
import fi.kroon.vadret.domain.AlertUseCase
import fi.kroon.vadret.presentation.viewmodel.AlertViewModel
import io.reactivex.Single
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.doReturn
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class AlertViewModelTest {

    @Mock
    lateinit var mockAlertUseCase: AlertUseCase

    @Mock
    lateinit var mockAlert: Alert

    private lateinit var testAlertViewModel: AlertViewModel

    private val testThrowable = Throwable()

    @Before
    fun setup() {
        testAlertViewModel = AlertViewModel(mockAlertUseCase)
    }

    @Test
    fun useCaseReturnsSingle_shouldPassResult() {
        val testWeatherEither = createWeatherEither(mockAlert)
        val testSingle = createWeatherSingle(testWeatherEither)
        doReturn(testSingle).`when`(mockAlertUseCase).get()

        testAlertViewModel
            .get()
            .test()
            .assertResult(testWeatherEither)
    }

    @Test
    fun useCaseReturnsFailure_shouldPassFailure() {
        val testFailureEither = createFailureEither()
        val testSingle = createFailureSingle(testFailureEither)
        doReturn(testSingle).`when`(mockAlertUseCase).get()

        testAlertViewModel
            .get()
            .test()
            .assertResult(testFailureEither)
    }

    @Test
    fun useCaseThrowsException_shouldReturnIOExceptionEither() {
        val testSingle = createThrowableSingle()
        doReturn(testSingle).`when`(mockAlertUseCase).get()

        testAlertViewModel
            .get()
            .test()
            .assertComplete()
            .assertNoErrors()
            .assertValueAt(0) { it is Either.Left<Failure> && it.a is Failure.IOException }
    }

    private fun createFailureEither() =
        Either.Left(Failure.IOException())

    private fun createFailureSingle(failureEither: Either.Left<Failure>) =
        Single.just(failureEither)

    private fun createThrowableSingle() =
        Single.error<Either<Failure, Alert>>(testThrowable)

    private fun createWeatherEither(mockWeather: Alert) =
        Either.Right(mockWeather) as Either<Failure, Alert>

    private fun createWeatherSingle(value: Either<Failure, Alert>) =
        Single.just(value)
}