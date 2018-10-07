package fi.kroon.vadret.presentation

import fi.kroon.vadret.data.exception.Either
import fi.kroon.vadret.data.exception.Failure
import fi.kroon.vadret.domain.SuggestionUseCase
import fi.kroon.vadret.presentation.viewmodel.SuggestionViewModel
import fi.kroon.vadret.util.RxImmediateSchedulerRule
import fi.kroon.vadret.utils.Schedulers
import io.reactivex.Single
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.doReturn
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class SuggestionViewModelTest {

    @Mock
    lateinit var mockSuggestionUseCase: SuggestionUseCase

    @Mock
    lateinit var mockStringList: List<String>

    private lateinit var mockSuggestionViewModel: SuggestionViewModel

    private lateinit var schedulers: Schedulers

    private val testThrowable = Throwable()

    private var query = "test"

    @Rule
    @JvmField var testSchedulerRule = RxImmediateSchedulerRule()

    @Before
    fun setup() {
        schedulers = Schedulers()
        mockSuggestionViewModel = SuggestionViewModel(schedulers, mockSuggestionUseCase)
    }

    /*@Test
    fun `suggestion viewmodel method returns correct result`() {
        val testStringList = Either.Right(mockStringList) as Either<Failure, List<String>>
        val testStringListSingle = Single.just(testStringList)

        doReturn(testStringListSingle)
            .`when`(mockSuggestionUseCase)
            .get()

        mockSuggestionViewModel
            .filter(query)
            .test()
            .assertComplete()
    }*/

    @Test
    fun `suggestion viewmodel get method returns failure`() {
        val testFailure = Either.Left(Failure.IOException())
        val testFailureSingle = Single.just(testFailure)

        doReturn(testFailureSingle)
            .`when`(mockSuggestionUseCase)
            .get()

        mockSuggestionViewModel
            .invoke(query)
            .test()
            .assertComplete()
    }

    /*@Test
    fun `suggestion viewmodel get method propagates exception`() {
        val testThrowableSingle = Single.error<Either<Failure, List<String>>>(testThrowable)
        doReturn(testThrowableSingle)
            .`when`(mockSuggestionUseCase)
            .get()

        mockSuggestionViewModel
            .filter(query)
            .test()
            .assertError(Throwable::class.java)
    }*/
}