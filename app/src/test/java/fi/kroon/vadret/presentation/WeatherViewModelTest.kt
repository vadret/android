package fi.kroon.vadret.presentation

import fi.kroon.vadret.data.weather.WeatherRequest
import fi.kroon.vadret.data.exception.Either
import fi.kroon.vadret.data.exception.Failure
import fi.kroon.vadret.data.weather.model.Weather
import fi.kroon.vadret.domain.WeatherUseCase
import fi.kroon.vadret.presentation.viewmodel.WeatherViewModel
import io.reactivex.Single
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.doReturn
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class WeatherViewModelTest {
    @Mock
    lateinit var mockWeatherUseCase: WeatherUseCase

    @Mock
    lateinit var mockWeatherRequest: WeatherRequest

    @Mock
    lateinit var mockWeather: Weather

    private lateinit var testViewModel: WeatherViewModel

    private val testThrowable = Throwable()

    @Before
    fun setup() {
        testViewModel = WeatherViewModel(mockWeatherUseCase)
    }

    @Test
    fun useCaseReturnsSingle_shouldPassResult() {
        val testWeatherEither = createWeatherEither(mockWeather)
        val testSingle = createWeatherSingle(testWeatherEither)
        doReturn(testSingle).`when`(mockWeatherUseCase).get(mockWeatherRequest)

        testViewModel
            .get(mockWeatherRequest)
            .test()
            .assertResult(testWeatherEither)
    }

    @Test
    fun useCaseReturnsFailure_shouldPassFailure() {
        val testFailureEither = createFailureEither()
        val testSingle = createFailureSingle(testFailureEither)
        doReturn(testSingle).`when`(mockWeatherUseCase).get(mockWeatherRequest)

        testViewModel
            .get(mockWeatherRequest)
            .test()
            .assertResult(testFailureEither)
    }

    @Test
    fun useCaseThrowsException_shouldReturnIOExceptionEither() {
        val testSingle = createThrowableSingle()
        doReturn(testSingle).`when`(mockWeatherUseCase).get(mockWeatherRequest)

        testViewModel
            .get(mockWeatherRequest)
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
        Single.error<Either<Failure, Weather>>(testThrowable)

    private fun createWeatherEither(mockWeather: Weather) =
        Either.Right(mockWeather) as Either<Failure, Weather>

    private fun createWeatherSingle(value: Either<Failure, Weather>) =
        Single.just(value)
}