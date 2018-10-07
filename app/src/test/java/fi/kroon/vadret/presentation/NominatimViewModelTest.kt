package fi.kroon.vadret.presentation

import fi.kroon.vadret.data.exception.Either
import fi.kroon.vadret.data.exception.Failure
import fi.kroon.vadret.data.nominatim.NominatimRequest
import fi.kroon.vadret.data.nominatim.NominatimRequestReverse
import fi.kroon.vadret.data.nominatim.exception.NominatimFailure
import fi.kroon.vadret.data.nominatim.model.Nominatim
import fi.kroon.vadret.domain.NominatimUseCase
import fi.kroon.vadret.presentation.viewmodel.NominatimViewModel
import io.reactivex.Single
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.doReturn
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class NominatimViewModelTest {

    @Mock
    lateinit var mockNominatimUseCase: NominatimUseCase

    @Mock
    lateinit var mockNominatim: Nominatim

    @Mock
    private lateinit var mockNominatimRequest: NominatimRequest

    @Mock
    private lateinit var mockNominatimRequestReverse: NominatimRequestReverse

    private lateinit var testNominatimViewModel: NominatimViewModel

    private val testThrowable = Throwable()

    @Before
    fun setup() {
        testNominatimViewModel = NominatimViewModel(mockNominatimUseCase)
    }

    @Test
    fun `nominatim viewmodel get returns correct result`() {
        val testNominatim = Either.Right(listOf(mockNominatim)) as Either<Failure, List<Nominatim>>
        val testNominatimSingle = Single.just(testNominatim)
        doReturn(testNominatimSingle)
            .`when`(mockNominatimUseCase)
            .get(mockNominatimRequest)

        testNominatimViewModel
            .get(mockNominatimRequest)
            .test()
            .assertResult(testNominatim)
    }

    @Test
    fun `nominatim viewmodel get returns failure`() {
        val testNominatimFailure = Either.Left(Failure.IOException())
        val testNominatimFailureSingle = Single.just(testNominatimFailure)

        doReturn(testNominatimFailureSingle).`when`(mockNominatimUseCase)
            .get(mockNominatimRequest)

        testNominatimViewModel
            .get(mockNominatimRequest)
            .test()
            .assertResult(testNominatimFailure)
    }

    @Test
    fun `nominatim viewmodel propagates exception`() {
        val testThrowableSingle = Single.error<Either<Failure, List<Nominatim>>>(testThrowable)
        doReturn(testThrowableSingle).`when`(mockNominatimUseCase)
            .get(mockNominatimRequest)

        testNominatimViewModel
            .get(mockNominatimRequest)
            .test()
            .assertComplete()
            .assertNoErrors()
            .assertValueAt(0) { it is Either.Left<Failure> && it.a is NominatimFailure.NominatimNotAvailable }
    }

    // reverse

    @Test
    fun `nominatim viewmodel reverse returns correct result`() {
        val testNominatim = Either.Right(mockNominatim) as Either<Failure, Nominatim>
        val testNominatimSingle = Single.just(testNominatim)
        doReturn(testNominatimSingle)
            .`when`(mockNominatimUseCase)
            .reverse(mockNominatimRequestReverse)

        testNominatimViewModel
            .reverse(mockNominatimRequestReverse)
            .test()
            .assertResult(testNominatim)
    }

    @Test
    fun `nominatim viewmodel reverse returns failure`() {
        val testNominatimFailure = Either.Left(Failure.IOException())
        val testNominatimFailureSingle = Single.just(testNominatimFailure)

        doReturn(testNominatimFailureSingle).`when`(mockNominatimUseCase)
            .reverse(mockNominatimRequestReverse)

        testNominatimViewModel
            .reverse(mockNominatimRequestReverse)
            .test()
            .assertResult(testNominatimFailure)
    }

    @Test
    fun `nominatim reverse viewmodel propagates exception`() {
        val testThrowableSingle = Single.error<Either<Failure, List<Nominatim>>>(testThrowable)
        doReturn(testThrowableSingle).`when`(mockNominatimUseCase)
            .reverse(mockNominatimRequestReverse)

        testNominatimViewModel
            .reverse(mockNominatimRequestReverse)
            .test()
            .assertComplete()
            .assertNoErrors()
            .assertValueAt(0) { it is Either.Left<Failure> && it.a is NominatimFailure.NominatimNotAvailable }
    }
}