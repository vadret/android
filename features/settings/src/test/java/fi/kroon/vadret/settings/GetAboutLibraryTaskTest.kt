package fi.kroon.vadret.settings

import fi.kroon.vadret.data.failure.Failure
import fi.kroon.vadret.data.library.LibraryRepository
import fi.kroon.vadret.data.library.model.Library
import fi.kroon.vadret.util.extension.asRight
import io.github.sphrak.either.Either
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertTrue
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