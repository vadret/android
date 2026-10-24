package fi.kroon.vadret.settings

import fi.kroon.vadret.data.failure.Failure
import fi.kroon.vadret.data.library.LibraryRepository
import fi.kroon.vadret.data.library.model.Library
import fi.kroon.vadret.util.extension.asRight
import io.github.sphrak.either.Either
import kotlinx.coroutines.test.runTest
import org.mockito.Mockito.doReturn
import org.mockito.Mockito.mock
import kotlin.test.Test
import kotlin.test.assertTrue

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

    private val mockLibraryRepository: LibraryRepository = mock(LibraryRepository::class.java)

    private val testGetAboutLibraryTask: GetAboutLibraryTask = GetAboutLibraryTask(libraryRepository = mockLibraryRepository)

    @Test
    fun `load valid library list`() = runTest {

        doReturn(getEither())
            .`when`(mockLibraryRepository)
            .invoke()

        assertTrue(
            testGetAboutLibraryTask()
                .let {
                    it is Either.Right && it.b == list
                }
        )
    }

    private fun getEither(): Either<Failure, List<Library>> =
        list.asRight()
}