package fi.kroon.vadret.data.weather

import fi.kroon.vadret.data.exception.Either
import fi.kroon.vadret.data.exception.Failure
import fi.kroon.vadret.data.weather.model.Weather
import fi.kroon.vadret.data.weather.net.WeatherApi
import fi.kroon.vadret.utils.NetworkHandler
import io.reactivex.Single
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.doReturn
import org.mockito.Mockito.doThrow
import org.mockito.junit.MockitoJUnitRunner
import retrofit2.Response

@RunWith(MockitoJUnitRunner::class)
class WeatherRepositoryTest {
    @Mock
    private lateinit var mockWeatherApi: WeatherApi

    @Mock
    private lateinit var mockNetworkHandler: NetworkHandler

    @Mock
    private lateinit var mockResponse: Response<Weather>

    @Mock
    private lateinit var mockWeather: Weather

    private lateinit var testWeatherRepository: WeatherRepository

    @Before
    fun setup() {
        testWeatherRepository = WeatherRepository(mockWeatherApi, mockNetworkHandler)
    }

    @Test
    fun networkHandlerIsNotConnected_shouldReturnResponse() {
        val testRequest = createTestRequest()
        doReturn(false).`when`(mockNetworkHandler).isConnected
        doReturn(mockWeather).`when`(mockResponse).body()
        doReturn(Single.just(mockResponse)).`when`(mockWeatherApi)
            .get(testRequest.category, testRequest.version, testRequest.longitude, testRequest.latitude)

        testWeatherRepository
            .get(testRequest)
            .test()
            .assertComplete()
            .assertNoErrors()
            .assertValueAt(0) { it is Either.Right<Weather> && it.b == mockWeather }
    }

    @Test
    fun weatherApiThrowsError_shouldReturnNetworkExceptionFailureSingle() {
        val testRequest = createTestRequest()
        doReturn(true).`when`(mockNetworkHandler).isConnected
        doThrow(RuntimeException()).`when`(mockWeatherApi)
            .get(testRequest.category, testRequest.version, testRequest.longitude, testRequest.latitude)

        testWeatherRepository
            .get(testRequest)
            .test()
            .assertComplete()
            .assertNoErrors()
            .assertValueAt(0) { it is Either.Left<Failure> && it.a is Failure.NetworkException }
    }

    @Test
    fun weatherApiReturnsNull_shouldReturnNetworkExceptionFailureSingle() {
        val testRequest = createTestRequest()
        doReturn(true).`when`(mockNetworkHandler).isConnected
        doReturn(null).`when`(mockWeatherApi)
            .get(testRequest.category, testRequest.version, testRequest.longitude, testRequest.latitude)

        testWeatherRepository
            .get(testRequest)
            .test()
            .assertComplete()
            .assertNoErrors()
            .assertValueAt(0) { it is Either.Left<Failure> && it.a is Failure.NetworkException }
    }

    @Test
    fun weatherApiReturnsWeather_shouldReturnWeatherEitherSingle() {
        val testRequest = createTestRequest()
        doReturn(true).`when`(mockNetworkHandler).isConnected
        doReturn(mockWeather).`when`(mockResponse).body()
        doReturn(Single.just(mockResponse)).`when`(mockWeatherApi)
            .get(testRequest.category, testRequest.version, testRequest.longitude, testRequest.latitude)

        testWeatherRepository
            .get(testRequest)
            .test()
            .assertComplete()
            .assertNoErrors()
            .assertValueAt(0) { it is Either.Right<Weather> && it.b == mockWeather }
    }

    private fun createTestRequest() =
        WeatherRequest()
}