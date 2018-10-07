package fi.kroon.vadret.data.radar

import fi.kroon.vadret.data.exception.Either
import fi.kroon.vadret.data.exception.Failure
import fi.kroon.vadret.data.radar.model.Radar
import fi.kroon.vadret.data.radar.net.RadarApi
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
class RadarRepositoryTest {

    @Mock
    private lateinit var mockRadarApi: RadarApi

    @Mock
    private lateinit var mockNetworkHandler: NetworkHandler

    @Mock
    private lateinit var mockResponse: Response<Radar>

    @Mock
    private lateinit var mockRadar: Radar

    private lateinit var testRadarRepository: RadarRepository

    @Before
    fun setup() {
        testRadarRepository = RadarRepository(mockRadarApi, mockNetworkHandler)
    }

    @Test
    fun `network not connected, should return network offline failure`() {
        val testRequest = RadarRequest()

        doReturn(false).`when`(mockNetworkHandler).isConnected

        testRadarRepository
            .get(testRequest)
            .test()
            .assertComplete()
            .assertNoErrors()
            .assertValueAt(0) { it is Either.Left<Failure> && it.a is Failure.NetworkOfflineFailure }
    }

    @Test
    fun `radarApi fails and throws network exception failure`() {
        val testRequest = RadarRequest()

        doReturn(true).`when`(mockNetworkHandler).isConnected
        doThrow(RuntimeException()).`when`(mockRadarApi)
            .get(testRequest.year, testRequest.month, testRequest.date, testRequest.format, testRequest.timeZone)

        testRadarRepository
            .get(testRequest)
            .test()
            .assertComplete()
            .assertNoErrors()
            .assertValueAt(0) { it is Either.Left<Failure> && it.a is Failure.NetworkException }
    }

    @Test
    fun `radarApi returns null response, should return network exception failure`() {
        val testRequest = RadarRequest()

        doReturn(true).`when`(mockNetworkHandler).isConnected
        doReturn(null).`when`(mockRadarApi)
            .get(testRequest.year, testRequest.month, testRequest.date, testRequest.format, testRequest.timeZone)

        testRadarRepository
            .get(testRequest)
            .test()
            .assertComplete()
            .assertNoErrors()
            .assertValueAt(0) { it is Either.Left<Failure> && it.a is Failure.NetworkException }
    }

    @Test
    fun `radarApi succeeds and returns Radar response`() {
        val testRequest = RadarRequest()

        doReturn(true).`when`(mockNetworkHandler).isConnected
        doReturn(mockRadar).`when`(mockResponse).body()
        doReturn(Single.just(mockResponse)).`when`(mockRadarApi)
            .get(testRequest.year, testRequest.month, testRequest.date, testRequest.format, testRequest.timeZone)

        testRadarRepository
            .get(testRequest)
            .test()
            .assertComplete()
            .assertNoErrors()
            .assertValueAt(0) { it is Either.Right<Radar> && it.b == mockRadar }
    }
}