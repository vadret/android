package fi.kroon.vadret.presentation.aboutapp

import android.content.Context
import fi.kroon.vadret.R
import fi.kroon.vadret.data.aboutinfo.model.AboutInfo
import fi.kroon.vadret.data.failure.Failure
import fi.kroon.vadret.domain.aboutapp.GetAboutInfoTask
import fi.kroon.vadret.presentation.aboutapp.about.AboutAppAboutView
import fi.kroon.vadret.presentation.aboutapp.about.AboutAppAboutViewModel
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
import org.mockito.Mockito.mock
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class AboutAppAboutViewModelTest {

    private val aboutInfoList: List<AboutInfo> = listOf(
        AboutInfo(
            iconResourceId = 0,
            titleResourceId = R.string.source_code_row_title,
            urlResourceId = R.string.app_release_page,
            hintResourceId = 0
        ),
        AboutInfo(
            iconResourceId = -1,
            titleResourceId = -1,
            urlResourceId = null,
            hintResourceId = -1
        )
    )

    private val mockContext: Context = mock(Context::class.java)

    private val initialState = AboutAppAboutView.State()

    private lateinit var testAboutAppAboutViewModel: AboutAppAboutViewModel

    @Mock
    private lateinit var mockGetAboutInfoTask: GetAboutInfoTask

    @Before
    fun setup() {
        testAboutAppAboutViewModel = AboutAppAboutViewModel(
            state = initialState,
            context = mockContext,
            getAboutInfoTask = mockGetAboutInfoTask
        )
    }

    @Test
    fun `send initialise event and receive render event display info`() {

        doReturn(getResultEither())
            .`when`(mockGetAboutInfoTask)
            .invoke()

        AboutAppAboutView
            .Event
            .OnViewInitialised
            .asObservable()
            .compose(testAboutAppAboutViewModel())
            .test()
            .assertNoErrors()
            .assertComplete()
            .assertValueAt(0) { receivedState: AboutAppAboutView.State ->
                receivedState.renderEvent is AboutAppAboutView.RenderEvent.DisplayInfo
            }
    }

    @Test
    fun `send on click event and receive render event open url`() {

        doReturn("some string").`when`(mockContext).getString(aboutInfoList[0].urlResourceId!!)

        AboutAppAboutView
            .Event
            .OnItemClick(aboutInfoList.first())
            .asObservable()
            .compose(testAboutAppAboutViewModel())
            .test()
            .assertNoErrors()
            .assertComplete()
            .assertValueAt(0) { receivedState: AboutAppAboutView.State ->
                receivedState.renderEvent is AboutAppAboutView.RenderEvent.OpenUrl
            }
    }

    @Test
    fun `send on click event and but match no resource receive render event none`() {
        AboutAppAboutView
            .Event
            .OnItemClick(aboutInfoList[1])
            .asObservable()
            .compose(testAboutAppAboutViewModel())
            .test()
            .assertNoErrors()
            .assertComplete()
            .assertValueAt(0) { receivedState: AboutAppAboutView.State ->
                receivedState.renderEvent is AboutAppAboutView.RenderEvent.None
            }
    }

    private fun getResultEither(): Single<Either<Failure, List<AboutInfo>>> =
        aboutInfoList
            .asRight()
            .asSingle()
}