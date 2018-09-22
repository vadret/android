package fi.kroon.vadret.data.weather

/*import fi.kroon.vadret.data.exception.Either
import fi.kroon.vadret.data.weather.exception.WeatherFailure
import fi.kroon.vadret.data.weather.WeatherMapper
import fi.kroon.vadret.data.weather.WeatherRepository
import fi.kroon.vadret.data.weather.model.Weather
import fi.kroon.vadret.data.weather.model.Parameter
import fi.kroon.vadret.data.weather.model.Geometry
import fi.kroon.vadret.data.weather.model.TimeSerie
import fi.kroon.vadret.data.weather.net.WeatherApi
import fi.kroon.vadret.utils.NetworkHandler
import org.junit.Before
import org.junit.Test
import org.mockito.ArgumentMatchers
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.`when`
import org.mockito.Mockito.verify
import org.mockito.Mockito.verifyNoMoreInteractions
import org.mockito.Mockito.times
import org.mockito.MockitoAnnotations


class WeatherRepositoryTest {

    private lateinit var weatherRepository: WeatherRepository

    @Mock
    private lateinit var weatherApi: WeatherApi

    @Mock
    private lateinit var weatherMapper: WeatherMapper

    @Mock
    private lateinit var networkHandler: NetworkHandler

    val request = Request(longitude = 59.3293, latitude = 18.0686)

    @Before
    @Throws(Exception::class)
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        weatherRepository = WeatherRepository(weatherApi, networkHandler)
    }

    @Test
    @Throws(Exception::class)
    fun `fetching weather data fails`() {
        val failure = WeatherFailure()
        `when`(weatherApi.get(request.longitude, request.latitude)).thenReturn(failure)
        weatherRepository.get(request).test().assertError(failure)
        verify(weatherApi, Mockito.times(1)).get(request.longitude, request.latitude)
        verifyNoMoreInteractions(weatherApi)
    }

    @Test
    @Throws(Exception::class)
    fun `fetching weather data succeeds`() {
        val response = listOf(
            Weather(
                approvedTime = "2018-09-20T04:37:37Z",
                geometry = Geometry(
                    type = "Point",
                    coordinates = listOf(listOf(59.3293), listOf(18.0686))
                ),
                referenceTime = "2018-09-19T15:00:00Z",
                timeSeries = listOf(
                    TimeSerie(
                        validTime = "2018-09-20T03:00:00Z",
                        parameters = listOf(
                            Parameter(
                                name = "Wsymb2",
                                level = "0",
                                levelType = "hl",
                                unit = "category",
                                values = listOf(
                                    111.11
                                )
                            )
                        )
                    )
                )
            )
        )

        `when`(weatherApi.get(ArgumentMatchers.anyDouble(), ArgumentMatchers.anyDouble())).thenReturn(response)

        weatherRepository.get(request).test().assertValue(Either.Right(response))
        verify(weatherApi, times(1)).get(request.longitude, request.latitude)
        verifyNoMoreInteractions(weatherApi)
    }
}*/