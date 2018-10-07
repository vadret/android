package fi.kroon.vadret.data.suggestion

import fi.kroon.vadret.R
import fi.kroon.vadret.data.common.RawTextFileReader
import fi.kroon.vadret.data.exception.Either
import fi.kroon.vadret.data.exception.Failure
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class SuggestionRepositoryTest {

    @Mock
    lateinit var rawTextFileReader: RawTextFileReader

    private lateinit var suggestionRepository: SuggestionRepository

    private val mockStringList = listOf("test", "test2")

    @Before
    fun setup() {
        suggestionRepository = SuggestionRepository(rawTextFileReader)
    }

    @Test
    fun `suggestions repository returns data successfully`() {
        Mockito.doReturn(Either.Right(mockStringList))
            .`when`(rawTextFileReader)
            .readFileAsList(R.raw.sweden)

        suggestionRepository
            .get()
            .test()
            .assertValueAt(0) { it is Either.Right<List<String>> && it.b == mockStringList }
    }

    @Test
    fun `suggestion repository fails to load file list`() {
        Mockito.doReturn(Either.Left(SuggestionFailure.SuggestionsNotAvailable()))
            .`when`(rawTextFileReader)
            .readFileAsList(R.raw.sweden)

        suggestionRepository
            .get()
            .test()
            .assertValueAt(0) { it is Either.Left<Failure> && it.a is SuggestionFailure.SuggestionsNotAvailable }
    }
}