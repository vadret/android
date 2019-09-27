package fi.kroon.vadret.presentation.aboutapp

import fi.kroon.vadret.data.failure.Failure
import fi.kroon.vadret.data.library.model.Library
import fi.kroon.vadret.domain.aboutapp.GetAboutLibraryTask
import fi.kroon.vadret.presentation.aboutapp.library.AboutAppLibraryView
import fi.kroon.vadret.presentation.aboutapp.library.AboutAppLibraryViewModel
import fi.kroon.vadret.util.extension.asObservable
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
class AboutAppLibraryViewModelTest {

    private val initialState = AboutAppLibraryView.State()

    private val list: List<Library> = listOf(
        Library(
            author = "Fakee",
            title = "Real Fake Libraries",
            projectUrl = "https://example.tld/",
            sourceUrl = "https://example.tld/",
            licenseUrl = "https://example.tld/",
            license = "Fake license",
            description = "Real fake libraries"
        )
    )

    private lateinit var testAboutAppLibraryViewModel: AboutAppLibraryViewModel

    @Mock
    private lateinit var mockGetAboutLibrary: GetAboutLibraryTask

    @Before
    fun setup() {
        testAboutAppLibraryViewModel =
            AboutAppLibraryViewModel(
                state = initialState,
                getAboutLibraryTask = mockGetAboutLibrary
            )
    }

    @Test
    fun `send initialise event and receive render event display library`() {

        doReturn(getResultEither()).`when`(mockGetAboutLibrary).invoke()

        AboutAppLibraryView
            .Event
            .OnViewInitialised
            .asObservable()
            .compose(testAboutAppLibraryViewModel())
            .test()
            .assertNoErrors()
            .assertComplete()
            .assertValueAt(0) { receivedState: AboutAppLibraryView.State ->
                receivedState.renderEvent is
                    AboutAppLibraryView.RenderEvent.DisplayLibrary
            }
    }

    @Test
    fun `send initialise event and receive render event open url`() {

        AboutAppLibraryView
            .Event
            .OnLicenseUrlClick(item = list[0])
            .asObservable()
            .compose(testAboutAppLibraryViewModel())
            .test()
            .assertNoErrors()
            .assertComplete()
            .assertValueAt(0) { receivedState: AboutAppLibraryView.State ->
                receivedState.renderEvent is
                    AboutAppLibraryView.RenderEvent.OpenUrl
            }
    }

    private fun getResultEither(): Single<Either<Failure, List<Library>>> =
        list.asRight()
            .asSingle()
}