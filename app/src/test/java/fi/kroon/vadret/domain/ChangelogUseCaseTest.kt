package fi.kroon.vadret.domain

import fi.kroon.vadret.data.changelog.ChangelogRepository
import fi.kroon.vadret.data.changelog.exception.ChangelogFailure
import fi.kroon.vadret.data.exception.Either
import fi.kroon.vadret.data.exception.Failure
import io.reactivex.Single
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class ChangelogUseCaseTest {
    @Mock
    lateinit var mockRepository: ChangelogRepository

    private lateinit var testUseCase: ChangelogUseCase

    private val testChangelogMessage: String = "TEST"

    @Before
    fun setup() {
        testUseCase = ChangelogUseCase(mockRepository)
    }

    @Test
    fun repositoryReturnsSingle_shouldPassResult() {
        val testMessage = createMessageEither()
        val testSingle = createMessageSingle(testMessage)
        Mockito.doReturn(testSingle).`when`(mockRepository).get()

        testUseCase
            .get()
            .test()
            .assertValueAt(0) { it is Either.Right<String> && it.b == testChangelogMessage }
    }

    @Test
    fun repositoryReturnsFailure_shouldPassFailure() {
        val testFailure = createFailureEither()
        val testSingle = createFailureSingle(testFailure)
        Mockito.doReturn(testSingle).`when`(mockRepository).get()

        testUseCase
            .get()
            .test()
            .assertResult(testFailure)
    }

    @Test
    fun repositoryThrowsException_shouldReturnIOExceptionEither() {
        val testSingle = createThrowableSingle()
        Mockito.doReturn(testSingle).`when`(mockRepository).get()

        testUseCase
            .get()
            .test()
            .assertValueAt(0) { it is Either.Left<Failure> && it.a is ChangelogFailure.FileNotAvailableFailure }
    }

    private fun createThrowableSingle() =
        Single.error<Throwable>(Throwable())

    private fun createFailureSingle(testFailure: Either<Failure, String>) =
        Single.just(testFailure)

    private fun createFailureEither() =
        Either.Left(ChangelogFailure.FileNotAvailableFailure())

    private fun createMessageSingle(testMessage: Either<Failure, String>) =
        Single.just(testMessage)

    private fun createMessageEither() =
        Either.Right(testChangelogMessage)
}