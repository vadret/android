package fi.kroon.vadret.data.nominatim

import fi.kroon.vadret.data.exception.Either
import fi.kroon.vadret.data.exception.Failure
import fi.kroon.vadret.data.nominatim.exception.NominatimFailure
import fi.kroon.vadret.data.nominatim.model.Nominatim
import fi.kroon.vadret.data.nominatim.net.NominatimApi
import fi.kroon.vadret.utils.NetworkHandler
import io.reactivex.Single
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.doReturn
import org.mockito.junit.MockitoJUnitRunner
import retrofit2.Response

@RunWith(MockitoJUnitRunner::class)
class NominatimRepositoryTest {

    @Mock
    private lateinit var mockNominatimApi: NominatimApi

    @Mock
    private lateinit var mockNetworkHandler: NetworkHandler

    @Mock
    private lateinit var mockResponse: Response<Nominatim>

    @Mock
    private lateinit var mockNominatim: Nominatim

    private lateinit var testNominatimRepository: NominatimRepository

    @Before
    fun setup() {
        testNominatimRepository = NominatimRepository(mockNominatimApi, mockNetworkHandler)
    }

    @Test
    fun `network handler not connected`() {

        val testRequest = NominatimRequest("Stockholm", addressDetails = 0)
        doReturn(false).`when`(mockNetworkHandler).isConnected

        testNominatimRepository
            .get(testRequest)
            .test()
            .assertComplete()
            .assertNoErrors()
            .assertValueAt(0) { it is Either.Left<Failure> && it.a is Failure.NetworkOfflineFailure }
    }

    @Test
    fun `nominatim api throws runtime exception`() {
        val testRequest = NominatimRequest("Stockholm", addressDetails = 0)

        doReturn(true).`when`(mockNetworkHandler).isConnected
        doReturn(Single.just(RuntimeException())).`when`(mockNominatimApi)
            .get(testRequest.city, testRequest.format, testRequest.countrycodes, testRequest.limit, testRequest.nameDetails, testRequest.addressDetails)

        testNominatimRepository
            .get(testRequest)
            .test()
            .assertComplete()
            .assertNoErrors()
            .assertValueAt(0) { it is Either.Left<Failure> && it.a is Failure.NetworkException }
    }

    @Test
    fun `nominatimApi returns empty response`() {
        val testRequest = NominatimRequest("Stockholm", addressDetails = 0)

        doReturn(true).`when`(mockNetworkHandler).isConnected
        doReturn(null).`when`(mockResponse).body()
        doReturn(Single.just(mockResponse)).`when`(mockNominatimApi)
            .get(testRequest.city, testRequest.format, testRequest.countrycodes, testRequest.limit, testRequest.nameDetails, testRequest.addressDetails)

        testNominatimRepository
            .get(testRequest)
            .test()
            .assertComplete()
            .assertNoErrors()
            .assertValueAt(0) { it is Either.Left<Failure> && it.a is NominatimFailure.NominatimNotAvailable }
    }

    @Test
    fun `nominatimApi returns actual response`() {
        val testRequest = NominatimRequest("Stockholm", addressDetails = 0)

        doReturn(true).`when`(mockNetworkHandler).isConnected
        doReturn(listOf(mockNominatim)).`when`(mockResponse).body()
        doReturn(Single.just(mockResponse)).`when`(mockNominatimApi)
            .get(testRequest.city, testRequest.format, testRequest.countrycodes, testRequest.limit, testRequest.nameDetails, testRequest.addressDetails)

        testNominatimRepository
            .get(testRequest)
            .test()
            .assertComplete()
            .assertNoErrors()
            .assertValueAt(0) { it is Either.Right<List<Nominatim>> && it.b == listOf(mockNominatim) }
    }

    /**
     *  Reverse Nominatim
     */

    @Test
    fun `reverse lookup fails because network is offline`() {
        val testRequest = NominatimRequestReverse(latitude = 1.01, longitude = 0.10)
        doReturn(false).`when`(mockNetworkHandler).isConnected

        testNominatimRepository
            .reverse(testRequest)
            .test()
            .assertComplete()
            .assertNoErrors()
            .assertValueAt(0) { it is Either.Left<Failure> && it.a is Failure.NetworkOfflineFailure }
    }

    @Test
    fun `reverse lookup throws error`() {
        val testRequest = NominatimRequestReverse(latitude = 1.01, longitude = 0.10)

        doReturn(true).`when`(mockNetworkHandler).isConnected
        doReturn(Single.just(RuntimeException())).`when`(mockNominatimApi)
            .reverse(testRequest.format, testRequest.latitude, testRequest.longitude, testRequest.zoom)

        testNominatimRepository
            .reverse(testRequest)
            .test()
            .assertComplete()
            .assertNoErrors()
            .assertValueAt(0) { it is Either.Left<Failure> && it.a is Failure.NetworkException }
    }

    @Test
    fun `reverse lookup response body is null`() {
        val testRequest = NominatimRequestReverse(latitude = 1.01, longitude = 0.10)
        doReturn(true).`when`(mockNetworkHandler).isConnected
        doReturn(null).`when`(mockResponse).body()
        doReturn(Single.just(mockResponse)).`when`(mockNominatimApi)
            .reverse(testRequest.format, testRequest.latitude, testRequest.longitude, testRequest.zoom)

        testNominatimRepository
            .reverse(testRequest)
            .test()
            .assertComplete()
            .assertNoErrors()
            .assertValueAt(0) { it is Either.Left<Failure> && it.a is NominatimFailure.NominatimNotAvailable }
    }

    @Test
    fun `reverse lookup response succeeds`() {
        val testRequest = NominatimRequestReverse(latitude = 1.01, longitude = 0.10)
        doReturn(true).`when`(mockNetworkHandler).isConnected
        doReturn(mockNominatim).`when`(mockResponse).body()
        doReturn(Single.just(mockResponse)).`when`(mockNominatimApi)
            .reverse(testRequest.format, testRequest.latitude, testRequest.longitude, testRequest.zoom)

        testNominatimRepository
            .reverse(testRequest)
            .test()
            .assertComplete()
            .assertNoErrors()
            .assertValueAt(0) { it is Either.Right<Nominatim> && it.b == mockNominatim }
    }
}