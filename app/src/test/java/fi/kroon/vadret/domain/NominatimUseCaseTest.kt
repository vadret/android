package fi.kroon.vadret.domain

import fi.kroon.vadret.data.exception.Either
import fi.kroon.vadret.data.exception.Failure
import fi.kroon.vadret.data.nominatim.NominatimRepository
import fi.kroon.vadret.data.nominatim.NominatimRequest
import fi.kroon.vadret.data.nominatim.NominatimRequestReverse
import fi.kroon.vadret.data.nominatim.model.Nominatim
import io.reactivex.Single
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.doReturn
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class NominatimUseCaseTest {

    @Mock
    private lateinit var mockNominatimRepository: NominatimRepository

    @Mock
    private lateinit var mockNominatimUseCase: NominatimUseCase

    @Mock
    private lateinit var mockNominatimRequest: NominatimRequest

    @Mock
    private lateinit var mockNominatimRequestReverse: NominatimRequestReverse

    @Mock
    private lateinit var mockNominatim: Nominatim

    private val testThrowable = Throwable()

    @Before
    fun setup() {
        mockNominatimUseCase = NominatimUseCase(mockNominatimRepository)
    }

    // for get()

    @Test
    fun `nominatim usecase returns a single`() {
        val testNominatim = Either.Right(listOf(mockNominatim)) as Either<Failure, List<Nominatim>>
        val testSingle = Single.just(testNominatim)
        doReturn(testSingle).`when`(mockNominatimRepository).get(mockNominatimRequest)

        mockNominatimUseCase
            .get(mockNominatimRequest)
            .test()
            .assertResult(testNominatim)
    }

    @Test
    fun `nominatim usecase propagates a network failure upstream`() {
        val testFailure = Either.Left(Failure.NetworkOfflineFailure())
        val testSingle = Single.just(testFailure)
        doReturn(testSingle).`when`(mockNominatimRepository).get(mockNominatimRequest)

        mockNominatimUseCase
            .get(mockNominatimRequest)
            .test()
            .assertResult(testFailure)
    }

    /*@Test
    fun `nominatim usecase returns a throwable`() {
        val testThrowableSingle = Single.error<Either<Failure, Nominatim>>(testThrowable)
        doReturn(testThrowableSingle)
            .`when`(mockNominatimRepository)
            .get(mockNominatimRequest)

        mockNominatimUseCase
            .get(mockNominatimRequest)
            .test()
            .assertError(Throwable::class.java)
    }*/

    // for reverse()

    @Test
    fun `nominatime reverse usecase returns single`() {
        val testNominatim = Either.Right(mockNominatim) as Either<Failure, Nominatim>
        val testSingle = Single.just(testNominatim)
        doReturn(testSingle).`when`(mockNominatimRepository).reverse(mockNominatimRequestReverse)

        mockNominatimUseCase
            .reverse(mockNominatimRequestReverse)
            .test()
            .assertResult(testNominatim)
    }

    @Test
    fun `nominatim reverse usecase propagates network failure upstream`() {
        val testFailure = Either.Left(Failure.NetworkOfflineFailure())
        val testFailureSingle = Single.just(testFailure)
        doReturn(testFailureSingle).`when`(mockNominatimRepository).reverse(mockNominatimRequestReverse)

        mockNominatimUseCase
            .reverse(mockNominatimRequestReverse)
            .test()
            .assertResult(testFailure)
    }

    /*@Test
    fun `nominatim reverse usecase returns a throwable`() {
        val testThrowableSingle = Single.error<Either<Failure, Nominatim>>(testThrowable)
        doReturn(testThrowableSingle)
            .`when`(mockNominatimRepository)
            .get(mockNominatimRequest)

        mockNominatimUseCase
            .get(mockNominatimRequest)
            .test()
            .assertError(Throwable::class.java)
    }*/
}