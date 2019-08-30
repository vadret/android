package fi.kroon.vadret.data.weatherforecast

import fi.kroon.vadret.data.exception.ErrorHandler
import fi.kroon.vadret.data.exception.Failure
import fi.kroon.vadret.data.weatherforecast.exception.WeatherForecastFailure
import fi.kroon.vadret.data.weatherforecast.model.Weather
import fi.kroon.vadret.data.weatherforecast.model.WeatherOut
import fi.kroon.vadret.data.weatherforecast.net.WeatherForecastNetDataSource
import fi.kroon.vadret.util.DEFAULT_LATITUDE
import fi.kroon.vadret.util.DEFAULT_LONGITUDE
import fi.kroon.vadret.util.HTTP_200_OK
import fi.kroon.vadret.util.HTTP_204_NO_CONTENT
import fi.kroon.vadret.util.HTTP_400_BAD_REQUEST
import fi.kroon.vadret.util.HTTP_403_FORBIDDEN
import fi.kroon.vadret.util.HTTP_404_NOT_FOUND
import fi.kroon.vadret.util.HTTP_500_INTERNAL_SERVER_ERROR
import fi.kroon.vadret.util.HTTP_503_SERVICE_UNAVAILABLE
import fi.kroon.vadret.util.HTTP_504_GATEWAY_TIMEOUT
import fi.kroon.vadret.util.NetworkHandler
import fi.kroon.vadret.util.extension.asSingle
import io.github.sphrak.either.Either
import io.reactivex.Single
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.doReturn
import org.mockito.junit.MockitoJUnitRunner
import retrofit2.Response

@RunWith(MockitoJUnitRunner::class)
class WeatherForecastRepositoryTest {

    private val errorHandler: ErrorHandler = ErrorHandler()

    private val weatherForecastRequest: WeatherOut = WeatherOut(
        latitude = DEFAULT_LATITUDE.toDouble(),
        longitude = DEFAULT_LONGITUDE.toDouble()
    )

    private lateinit var testWeatherForecastRepository: WeatherForecastRepository

    @Mock
    private lateinit var mockWeatherForecastNetDataSource: WeatherForecastNetDataSource

    @Mock
    private lateinit var mockNetworkHandler: NetworkHandler

    @Mock
    private lateinit var mockWeatherResponse: Response<Weather>

    @Mock
    private lateinit var mockWeatherForecast: Weather

    @Before
    fun setup() {
        testWeatherForecastRepository = WeatherForecastRepository(
            mockWeatherForecastNetDataSource,
            mockNetworkHandler,
            errorHandler = errorHandler
        )
    }

    @Test
    fun `repository returns WeatherIn object correctly`() {

        doReturn(HTTP_200_OK).`when`(mockWeatherResponse).code()
        doReturn(true).`when`(mockNetworkHandler).isConnected
        doReturn(mockWeatherForecast).`when`(mockWeatherResponse).body()
        doReturn(Single.just(mockWeatherResponse))
            .`when`(mockWeatherForecastNetDataSource)
            .get(
                weatherForecastRequest.category,
                weatherForecastRequest.version,
                weatherForecastRequest.longitude,
                weatherForecastRequest.latitude
            )

        testWeatherForecastRepository
            .get(weatherForecastRequest)
            .test()
            .assertComplete()
            .assertNoErrors()
            .assertValueAt(0) { it is Either.Right<Weather> && it.b == mockWeatherForecast }
    }

    @Test
    fun `repository returns not available when NOT_CONNECTED`() {

        doReturn(false).`when`(mockNetworkHandler).isConnected

        testWeatherForecastRepository
            .get(weatherForecastRequest)
            .test()
            .assertComplete()
            .assertNoErrors()
            .assertValueAt(0) { it is Either.Left<Failure> && it.a is Failure.NetworkOfflineError }
    }

    @Test
    fun `repository returns HTTP_204_NO_CONTENT failure`() {

        doReturn(HTTP_204_NO_CONTENT).`when`(mockWeatherResponse).code()
        doReturn(true).`when`(mockNetworkHandler).isConnected
        doReturn(mockWeatherResponse.asSingle())
            .`when`(mockWeatherForecastNetDataSource)
            .get(
                weatherForecastRequest.category,
                weatherForecastRequest.version,
                weatherForecastRequest.longitude,
                weatherForecastRequest.latitude
            )
        testWeatherForecastRepository
            .get(weatherForecastRequest)
            .test()
            .assertComplete()
            .assertNoErrors()
            .assertValueAt(0) { it is Either.Left<Failure> && it.a is WeatherForecastFailure.NoWeatherAvailable }
    }

    @Test
    fun `repository returns HTTP_403_FORBIDDEN failure`() {

        doReturn(HTTP_403_FORBIDDEN).`when`(mockWeatherResponse).code()
        doReturn(true).`when`(mockNetworkHandler).isConnected
        doReturn(Single.just(mockWeatherResponse))
            .`when`(mockWeatherForecastNetDataSource)
            .get(
                weatherForecastRequest.category,
                weatherForecastRequest.version,
                weatherForecastRequest.longitude,
                weatherForecastRequest.latitude
            )
        testWeatherForecastRepository
            .get(weatherForecastRequest)
            .test()
            .assertComplete()
            .assertNoErrors()
            .assertValueAt(0) { it is Either.Left<Failure> && it.a is Failure.HttpForbidden403 }
    }

    @Test
    fun `repository returns HTTP_404_NOT_FOUND failure`() {

        doReturn(HTTP_404_NOT_FOUND).`when`(mockWeatherResponse).code()
        doReturn(true).`when`(mockNetworkHandler).isConnected
        doReturn(Single.just(mockWeatherResponse))
            .`when`(mockWeatherForecastNetDataSource)
            .get(
                weatherForecastRequest.category,
                weatherForecastRequest.version,
                weatherForecastRequest.longitude,
                weatherForecastRequest.latitude
            )
        testWeatherForecastRepository
            .get(weatherForecastRequest)
            .test()
            .assertComplete()
            .assertNoErrors()
            .assertValueAt(0) { it is Either.Left<Failure> && it.a is WeatherForecastFailure.NoWeatherAvailableForThisLocation }
    }

    @Test
    fun `repository returns HTTP_400_BAD_REQUEST failure`() {

        doReturn(HTTP_400_BAD_REQUEST).`when`(mockWeatherResponse).code()
        doReturn(true).`when`(mockNetworkHandler).isConnected
        doReturn(Single.just(mockWeatherResponse))
            .`when`(mockWeatherForecastNetDataSource)
            .get(
                weatherForecastRequest.category,
                weatherForecastRequest.version,
                weatherForecastRequest.longitude,
                weatherForecastRequest.latitude
            )
        testWeatherForecastRepository
            .get(weatherForecastRequest)
            .test()
            .assertComplete()
            .assertNoErrors()
            .assertValueAt(0) { it is Either.Left<Failure> && it.a is Failure.HttpBadRequest400 }
    }

    @Test
    fun `repository returns HTTP_500_INTERNAL_SERVER_ERROR failure`() {

        doReturn(HTTP_500_INTERNAL_SERVER_ERROR).`when`(mockWeatherResponse).code()
        doReturn(true).`when`(mockNetworkHandler).isConnected
        doReturn(Single.just(mockWeatherResponse))
            .`when`(mockWeatherForecastNetDataSource)
            .get(
                weatherForecastRequest.category,
                weatherForecastRequest.version,
                weatherForecastRequest.longitude,
                weatherForecastRequest.latitude
            )
        testWeatherForecastRepository
            .get(weatherForecastRequest)
            .test()
            .assertComplete()
            .assertNoErrors()
            .assertValueAt(0) { it is Either.Left<Failure> && it.a is Failure.HttpInternalServerError500 }
    }

    @Test
    fun `repository returns HTTP_503_SERVICE_UNAVAILABLE failure`() {

        doReturn(HTTP_503_SERVICE_UNAVAILABLE).`when`(mockWeatherResponse).code()
        doReturn(true).`when`(mockNetworkHandler).isConnected
        doReturn(Single.just(mockWeatherResponse))
            .`when`(mockWeatherForecastNetDataSource)
            .get(
                weatherForecastRequest.category,
                weatherForecastRequest.version,
                weatherForecastRequest.longitude,
                weatherForecastRequest.latitude
            )
        testWeatherForecastRepository
            .get(weatherForecastRequest)
            .test()
            .assertComplete()
            .assertNoErrors()
            .assertValueAt(0) { it is Either.Left<Failure> && it.a is Failure.HttpServiceUnavailable503 }
    }

    @Test
    fun `repository returns HTTP_504_GATEWAY_TIMEOUT failure`() {

        doReturn(HTTP_504_GATEWAY_TIMEOUT).`when`(mockWeatherResponse).code()
        doReturn(true).`when`(mockNetworkHandler).isConnected
        doReturn(Single.just(mockWeatherResponse))
            .`when`(mockWeatherForecastNetDataSource)
            .get(
                weatherForecastRequest.category,
                weatherForecastRequest.version,
                weatherForecastRequest.longitude,
                weatherForecastRequest.latitude
            )
        testWeatherForecastRepository
            .get(weatherForecastRequest)
            .test()
            .assertComplete()
            .assertNoErrors()
            .assertValueAt(0) { it is Either.Left<Failure> && it.a is Failure.HttpGatewayTimeout504 }
    }
}