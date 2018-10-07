package fi.kroon.vadret.data.changelog

import fi.kroon.vadret.R
import fi.kroon.vadret.data.common.RawTextFileReader
import fi.kroon.vadret.data.common.exception.ReaderFailure
import fi.kroon.vadret.data.exception.Either
import fi.kroon.vadret.data.exception.Failure
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.doReturn
import org.mockito.junit.MockitoJUnitRunner
import java.io.IOException

@RunWith(MockitoJUnitRunner::class)
class ChangelogRepositoryTest {
    @Mock
    lateinit var mockReader: RawTextFileReader

    private lateinit var testRepository: ChangelogRepository

    private val testChangelogMessage: String = "TEST"

    @Before
    fun setup() {
        testRepository = ChangelogRepository(mockReader)
    }

    @Test
    fun readerReturnsString_shouldPassResult() {
        doReturn(Either.Right(testChangelogMessage))
            .`when`(mockReader)
            .readFile(R.raw.changelog)

        testRepository
            .get()
            .test()
            .assertValueAt(0) { it is Either.Right<String> && it.b == testChangelogMessage }
    }

    @Test
    fun providerReturnsFailure_shouldPassFailure() {
        doReturn(Either.Left(ReaderFailure.IOFailure(IOException())))
            .`when`(mockReader)
            .readFile(R.raw.changelog)

        testRepository
            .get()
            .test()
            .assertValueAt(0) { it is Either.Left<Failure> && it.a is ReaderFailure.IOFailure }
    }
}