package fi.kroon.vadret.domain

import fi.kroon.vadret.data.weather.WeatherRequest
import fi.kroon.vadret.data.exception.Either
import fi.kroon.vadret.data.exception.Failure
import fi.kroon.vadret.data.weather.WeatherRepository
import fi.kroon.vadret.data.weather.model.Weather
import io.reactivex.Single
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class WeatherUseCaseTest {
    @Mock
    lateinit var mockWeatherRepository: WeatherRepository

    @Mock
    lateinit var mockWeatherRequest: WeatherRequest

    @Mock
    lateinit var mockWeather: Weather

    private lateinit var testUseCase: WeatherUseCase

    private val testThrowable = Throwable()

    @Before
    fun setup() {
        testUseCase = WeatherUseCase(mockWeatherRepository)
    }

    @Test
    fun repositoryReturnsSingle_shouldPassResult() {
        val testWeatherEither = createWeatherEither(mockWeather)
        val testSingle = createWeatherSingle(testWeatherEither)
        Mockito.doReturn(testSingle).`when`(mockWeatherRepository).get(mockWeatherRequest)

        testUseCase
            .get(mockWeatherRequest)
            .test()
            .assertResult(testWeatherEither)
    }

    @Test
    fun repositoryReturnsFailure_shouldPassFailure() {
        val testFailureEither = createFailureEither()
        val testSingle = createFailureSingle(testFailureEither)
        Mockito.doReturn(testSingle).`when`(mockWeatherRepository).get(mockWeatherRequest)

        testUseCase
            .get(mockWeatherRequest)
            .test()
            .assertResult(testFailureEither)
    }

    @Test
    fun repositoryThrowsException_shouldReturnIOExceptionEither() {
        val testSingle = createThrowableSingle()
        Mockito.doReturn(testSingle).`when`(mockWeatherRepository).get(mockWeatherRequest)

        testUseCase
            .get(mockWeatherRequest)
            .test()
            .assertError(Throwable::class.java)
    }

    private fun createFailureEither() =
        Either.Left(Failure.NetworkOfflineFailure())

    private fun createFailureSingle(failureEither: Either.Left<Failure>) =
        Single.just(failureEither)

    private fun createThrowableSingle() =
        Single.error<Either<Failure, Weather>>(testThrowable)

    private fun createWeatherEither(mockWeather: Weather) =
        Either.Right(mockWeather) as Either<Failure, Weather>

    private fun createWeatherSingle(value: Either<Failure, Weather>) =
        Single.just(value)
}