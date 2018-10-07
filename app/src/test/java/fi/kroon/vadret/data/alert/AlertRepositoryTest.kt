package fi.kroon.vadret.data.alert

import fi.kroon.vadret.data.alert.model.Alert
import fi.kroon.vadret.data.exception.Either
import fi.kroon.vadret.data.exception.Failure
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
class AlertRepositoryTest {

    @Mock
    private lateinit var mockAlertApi: AlertApi

    @Mock
    private lateinit var mockNetworkHandler: NetworkHandler

    @Mock
    private lateinit var mockResponse: Response<Alert>

    @Mock
    private lateinit var mockAlert: Alert

    private lateinit var testAlertRepository: AlertRepository

    @Before
    fun setup() {
        testAlertRepository = AlertRepository(mockAlertApi, mockNetworkHandler)
    }

    @Test
    fun `networkHandler not connected should return networkofflinefailure exception`() {
        doReturn(false).`when`(mockNetworkHandler).isConnected

        testAlertRepository
            .get()
            .test()
            .assertComplete()
            .assertNoErrors()
            .assertValueAt(0) { it is Either.Left<Failure> && it.a is Failure.NetworkOfflineFailure }
    }

    @Test
    fun `alertApi throws runtime exception and should return network exception failure`() {
        doReturn(true).`when`(mockNetworkHandler).isConnected
        doReturn(Single.just(RuntimeException())).`when`(mockAlertApi).get()

        testAlertRepository
            .get()
            .test()
            .assertComplete()
            .assertNoErrors()
            .assertValueAt(0) { it is Either.Left<Failure> && it.a is Failure.NetworkException }
    }

    @Test
    fun `alertApi returns empty should return network exception failure`() {
        doReturn(true).`when`(mockNetworkHandler).isConnected
        doReturn(Single.just(mockResponse)).`when`(mockAlertApi).get()

        testAlertRepository
            .get()
            .test()
            .assertComplete()
            .assertNoErrors()
            .assertValueAt(0) { it is Either.Left<Failure> && it.a is Failure.NetworkException }
    }

    @Test
    fun `alertApi returns Alert`() {
        doReturn(true).`when`(mockNetworkHandler).isConnected
        doReturn(mockAlert).`when`(mockResponse).body()
        doReturn(Single.just(mockResponse)).`when`(mockAlertApi).get()

        testAlertRepository
            .get()
            .test()
            .assertComplete()
            .assertNoErrors()
            .assertValueAt(0) { it is Either.Right<Alert> && it.b == mockAlert }
    }
}