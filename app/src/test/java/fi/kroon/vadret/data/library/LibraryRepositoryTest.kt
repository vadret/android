package fi.kroon.vadret.data.library

import fi.kroon.vadret.data.functional.Either
import fi.kroon.vadret.data.exception.Failure
import fi.kroon.vadret.data.library.exception.LibraryFailure
import fi.kroon.vadret.data.library.local.LibraryLocalDataSource
import fi.kroon.vadret.BaseUnitTest
import fi.kroon.vadret.data.library.model.Library
import fi.kroon.vadret.utils.extensions.asLeft
import io.reactivex.Single
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.doReturn

class LibraryRepositoryTest : BaseUnitTest() {

    @Mock
    private lateinit var mockLibraryLocalDataSource: LibraryLocalDataSource

    private lateinit var testLibraryRepository: LibraryRepository

    @Mock
    private lateinit var mockLibraryList: List<Library>

    @Before
    fun setup() {
        testLibraryRepository = LibraryRepository(mockLibraryLocalDataSource)
    }

    @Test
    fun `repository returns expected objects correctly`() {
        doReturn(getLibraryList())
            .`when`(mockLibraryLocalDataSource)()

        testLibraryRepository()
            .test()
            .assertComplete()
            .assertNoErrors()
            .assertValueAt(0) { it is Either.Right<List<Library>> && it.b == mockLibraryList }
    }

    @Test
    fun `local data source returns no aboutinfo available to repository`() {
        doReturn(getLibraryListFailure())
            .`when`(mockLibraryLocalDataSource)()

        testLibraryRepository()
            .test()
            .assertComplete()
            .assertNoErrors()
            .assertValueAt(0) { it is Either.Left<Failure> && it.a is LibraryFailure.NoLibraryAvailable }
    }

    @Test
    fun `local data source throws exception repository propagate exception`() {
        doReturn(throwException())
            .`when`(mockLibraryLocalDataSource)()

        testLibraryRepository()
            .test()
            .assertComplete()
            .assertNoErrors()
            .assertValueAt(0) { it is Either.Left<Failure> && it.a is LibraryFailure.NoLibraryAvailable }
    }

    private fun throwException(): Single<Either<Failure, List<Library>>> =
        Single.error<Either<Failure, List<Library>>>(Exception("failure"))

    private fun getLibraryList(): Single<Either<Failure, List<Library>>> = Single.just(
        Either.Right(mockLibraryList)
    )

    private fun getLibraryListFailure(): Single<Either<Failure, List<Library>>> = Single.just(
        LibraryFailure.NoLibraryAvailable().asLeft()
    )
}