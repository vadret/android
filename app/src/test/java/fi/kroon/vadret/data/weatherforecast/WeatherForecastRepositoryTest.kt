package fi.kroon.vadret.data.weatherforecast

import fi.kroon.vadret.BaseUnitTest
import fi.kroon.vadret.data.functional.Either
import fi.kroon.vadret.data.exception.Failure
import fi.kroon.vadret.data.weatherforecast.exception.WeatherForecastFailure
import fi.kroon.vadret.data.weatherforecast.local.WeatherForecastLocalKeyValueDataSource
import fi.kroon.vadret.data.weatherforecast.net.WeatherForecastNetDataSource
import fi.kroon.vadret.data.weatherforecast.model.Weather
import fi.kroon.vadret.data.weatherforecast.model.WeatherOut
import fi.kroon.vadret.utils.CONNECTED
import fi.kroon.vadret.utils.DEFAULT_LATITUDE
import fi.kroon.vadret.utils.DEFAULT_LONGITUDE
import fi.kroon.vadret.utils.HTTP_200_OK
import fi.kroon.vadret.utils.HTTP_204_NO_CONTENT
import fi.kroon.vadret.utils.HTTP_400_BAD_REQUEST
import fi.kroon.vadret.utils.HTTP_403_FORBIDDEN
import fi.kroon.vadret.utils.HTTP_404_NOT_FOUND
import fi.kroon.vadret.utils.HTTP_500_INTERNAL_SERVER_ERROR
import fi.kroon.vadret.utils.HTTP_503_SERVICE_UNAVAILABLE
import fi.kroon.vadret.utils.HTTP_504_GATEWAY_TIMEOUT
import fi.kroon.vadret.utils.NOT_CONNECTED
import fi.kroon.vadret.utils.NetworkHandler
import fi.kroon.vadret.utils.extensions.asSingle
import io.reactivex.Single
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.doReturn
import retrofit2.Response

class WeatherForecastRepositoryTest : BaseUnitTest() {

    @Mock
    private lateinit var mockWeatherForecastNetDataSource: WeatherForecastNetDataSource

    @Mock
    private lateinit var mockWeatherForecastLocalKeyValueDataSource: WeatherForecastLocalKeyValueDataSource

    @Mock
    private lateinit var mockNetworkHandler: NetworkHandler

    @Mock
    private lateinit var mockWeatherResponse: Response<Weather>

    @Mock
    private lateinit var mockWeatherForecast: Weather

    private lateinit var testWeatherForecastRepository: WeatherForecastRepository

    private val weatherForecastRequest: WeatherOut = WeatherOut(
        latitude = DEFAULT_LATITUDE.toDouble(),
        longitude = DEFAULT_LONGITUDE.toDouble()
    )

    @Before
    fun setup() {
        testWeatherForecastRepository = WeatherForecastRepository(
            mockWeatherForecastNetDataSource,
            mockWeatherForecastLocalKeyValueDataSource,
            mockNetworkHandler
        )
    }

    @Test
    fun `repository returns WeatherIn object correctly`() {

        doReturn(HTTP_200_OK).`when`(mockWeatherResponse).code()
        doReturn(CONNECTED).`when`(mockNetworkHandler).isConnected
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

        doReturn(NOT_CONNECTED).`when`(mockNetworkHandler).isConnected

        testWeatherForecastRepository
            .get(weatherForecastRequest)
            .test()
            .assertComplete()
            .assertNoErrors()
            .assertValueAt(0) { it is Either.Left<Failure> && it.a is Failure.NetworkOfflineFailure }
    }

    @Test
    fun `repository returns HTTP_204_NO_CONTENT failure`() {

        doReturn(HTTP_204_NO_CONTENT).`when`(mockWeatherResponse).code()
        doReturn(CONNECTED).`when`(mockNetworkHandler).isConnected
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
        doReturn(CONNECTED).`when`(mockNetworkHandler).isConnected
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
        doReturn(CONNECTED).`when`(mockNetworkHandler).isConnected
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
        doReturn(CONNECTED).`when`(mockNetworkHandler).isConnected
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
        doReturn(CONNECTED).`when`(mockNetworkHandler).isConnected
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
        doReturn(CONNECTED).`when`(mockNetworkHandler).isConnected
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
        doReturn(CONNECTED).`when`(mockNetworkHandler).isConnected
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

    @Test
    fun `repository returns single exception failure`() {

        doReturn(CONNECTED).`when`(mockNetworkHandler).isConnected
        doReturn(throwException())
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
            .assertValueAt(0) { it is Either.Left<Failure> && it.a is Failure.NetworkException }
    }

    private fun throwException(): Single<Either<Failure, Weather>> =
        Single.error<Either<Failure, Weather>>(Exception("failure"))
}