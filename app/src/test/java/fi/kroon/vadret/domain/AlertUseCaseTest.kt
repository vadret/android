package fi.kroon.vadret.domain

import fi.kroon.vadret.data.alert.AlertRepository
import fi.kroon.vadret.data.alert.model.Alert
import fi.kroon.vadret.data.exception.Either
import fi.kroon.vadret.data.exception.Failure
import io.reactivex.Single
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.doReturn
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class AlertUseCaseTest {

    @Mock
    lateinit var mockAlertRepository: AlertRepository

    @Mock
    lateinit var mockAlert: Alert

    private lateinit var testUseCase: AlertUseCase

    private val throwable = Throwable()

    @Before
    fun setup() {
        testUseCase = AlertUseCase(mockAlertRepository)
    }

    @Test
    fun `repository returns alert`() {
        val testWeatherEither = createAlertEither(mockAlert)
        val testSingle = createAlertSingle(testWeatherEither)
        doReturn(testSingle).`when`(mockAlertRepository).get()

        testUseCase
            .get()
            .test()
            .assertResult(testWeatherEither)
    }

    @Test
    fun `repository should return failure`() {
        val testFailureEither = createFailureEither()
        val testSingle = createFailureSingle(testFailureEither)
        doReturn(testSingle).`when`(mockAlertRepository).get()

        testUseCase
            .get()
            .test()
            .assertResult(testFailureEither)
    }

    @Test
    fun repositoryThrowsException_shouldReturnIOExceptionEither() {
        val testSingle = createThrowableSingle()
        doReturn(testSingle).`when`(mockAlertRepository).get()

        testUseCase
            .get()
            .test()
            .assertError(Throwable::class.java)
    }

    private fun createThrowableSingle() =
        Single.error<Either<Failure, Alert>>(throwable)

    private fun createFailureEither() =
        Either.Left(Failure.NetworkOfflineFailure())

    private fun createFailureSingle(failureEither: Either.Left<Failure>) =
        Single.just(failureEither)

    private fun createAlertEither(mockWeather: Alert) =
        Either.Right(mockWeather) as Either<Failure, Alert>

    private fun createAlertSingle(value: Either<Failure, Alert>) =
        Single.just(value)
}