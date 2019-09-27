package fi.kroon.vadret.domain.aboutapp

import fi.kroon.vadret.data.failure.Failure
import fi.kroon.vadret.data.library.LibraryRepository
import fi.kroon.vadret.data.library.model.Library
import fi.kroon.vadret.util.extension.asRight
import fi.kroon.vadret.util.extension.asSingle
import io.github.sphrak.either.Either
import io.reactivex.Single
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.doReturn
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class GetAboutLibraryTaskTest {

    private val list: List<Library> = listOf(
        Library(
            author = "",
            title = "",
            projectUrl = "",
            sourceUrl = null,
            licenseUrl = "",
            license = "",
            description = ""
        )
    )

    private lateinit var testGetAboutLibraryTask: GetAboutLibraryTask

    @Mock
    private lateinit var mockLibraryRepository: LibraryRepository

    @Before
    fun setup() {
        testGetAboutLibraryTask =
            GetAboutLibraryTask(libraryRepository = mockLibraryRepository)
    }

    @Test
    fun `load valid library list`() {

        doReturn(getEither())
            .`when`(mockLibraryRepository)
            .invoke()

        testGetAboutLibraryTask()
            .test()
            .assertNoErrors()
            .assertComplete()
            .assertValueAt(0) {
                it is Either.Right && it.b == list
            }
    }

    private fun getEither(): Single<Either<Failure, List<Library>>> =
        list.asRight()
            .asSingle()
}