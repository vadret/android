package fi.kroon.vadret.domain

import fi.kroon.vadret.data.exception.Either
import fi.kroon.vadret.data.exception.Failure
import fi.kroon.vadret.data.suggestion.SuggestionRepository
import io.reactivex.Single
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.doReturn
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class SuggestionTaskTest {

    @Mock
    private lateinit var mockSuggestionRepository: SuggestionRepository

    @Mock
    private lateinit var mockSuggestionTask: SuggestionTask

    @Mock
    private lateinit var mockStringList: List<String>

    private val testThrowable = Throwable()

    @Before
    fun setup() {
        mockSuggestionTask = SuggestionTask(mockSuggestionRepository)
    }

    @Test
    fun `suggestion repository returns a single`() {
        val testStringList = Either.Right(mockStringList) as Either<Failure, List<String>>
        val testStringListSingle = Single.just(testStringList)

        doReturn(testStringListSingle)
            .`when`(mockSuggestionRepository)
            .get()

        mockSuggestionTask
            .invoke()
            .test()
            .assertResult(testStringList)
    }

    @Test
    fun `suggestion repository returns a failure`() {
        val testFailure = Either.Left(Failure.IOException())
        val testFailureSingle = Single.just(testFailure)

        doReturn(testFailureSingle)
            .`when`(mockSuggestionRepository)
            .get()

        mockSuggestionTask
            .invoke()
            .test()
            .assertResult(testFailure)
    }

    /*@Test
    fun `suggestion repository returns throwable`() {
        val testThrowableSingle = Single.error<Either<Failure, List<String>>>(testThrowable)

        doReturn(testThrowableSingle)
            .`when`(mockSuggestionRepository)
            .invoke()

        mockSuggestionTask
            .invoke()
            .test()
            .assertError(Throwable::class.java)
    }*/
}