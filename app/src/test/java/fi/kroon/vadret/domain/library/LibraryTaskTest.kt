package fi.kroon.vadret.domain.library

import fi.kroon.vadret.BaseUnitTest
import fi.kroon.vadret.data.functional.Either
import fi.kroon.vadret.data.exception.Failure
import fi.kroon.vadret.data.library.LibraryRepository
import fi.kroon.vadret.data.library.exception.LibraryFailure
import fi.kroon.vadret.data.library.model.Library
import fi.kroon.vadret.domain.LibraryTask
import fi.kroon.vadret.utils.extensions.asLeft
import io.reactivex.Single
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.doReturn

class LibraryTaskTest : BaseUnitTest() {

    @Mock
    private lateinit var mockLibraryRepository: LibraryRepository

    private lateinit var testLibraryTask: LibraryTask

    @Mock
    private lateinit var mockLibraryList: List<Library>

    @Before
    fun setup() {
        testLibraryTask = LibraryTask(mockLibraryRepository)
    }

    @Test
    fun `task returns expected objects`() {
        doReturn(getLibraryEntityList())
            .`when`(mockLibraryRepository)()

        val sortedMockLibraryList: List<Library> = mockLibraryList.sortedWith(
            compareBy { library: Library ->
                library.title
            }
        )

        testLibraryTask()
            .test()
            .assertComplete()
            .assertNoErrors()
            .assertValueAt(0) { it is Either.Right<List<Library>> && it.b == sortedMockLibraryList }
    }

    @Test
    fun `task throws internal error`() {
        doReturn(getLibraryEntityListFailure())
            .`when`(mockLibraryRepository)()

        testLibraryTask()
            .test()
            .assertComplete()
            .assertNoErrors()
            .assertValueAt(0) { it is Either.Left<Failure> && it.a is LibraryFailure.NoLibraryAvailable }
    }

    @Test
    fun `task throws exception`() {
        doReturn(throwException())
            .`when`(mockLibraryRepository)()

        testLibraryTask()
            .test()
            .assertComplete()
            .assertNoErrors()
            .assertValueAt(0) { it is Either.Left<Failure> && it.a is LibraryFailure.NoLibraryAvailable }
    }

    private fun throwException(): Single<Either<Failure, List<Library>>> =
        Single.error<Either<Failure, List<Library>>>(Exception("failure"))

    private fun getLibraryEntityList(): Single<Either<Failure, List<Library>>> = Single.just(
        Either.Right(mockLibraryList)
    )

    private fun getLibraryEntityListFailure(): Single<Either<Failure, List<Library>>> = Single.just(
        LibraryFailure.NoLibraryAvailable().asLeft()
    )
}