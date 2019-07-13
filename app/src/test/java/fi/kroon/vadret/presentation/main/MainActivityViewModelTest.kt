package fi.kroon.vadret.presentation.main

import fi.kroon.vadret.data.exception.Failure
import fi.kroon.vadret.data.functional.Either
import fi.kroon.vadret.domain.theme.GetThemeModeTask
import fi.kroon.vadret.domain.theme.ObserveThemeChangeTask
import fi.kroon.vadret.util.DARK_THEME
import fi.kroon.vadret.util.LIGHT_THEME
import fi.kroon.vadret.util.extension.asObservable
import fi.kroon.vadret.util.extension.asRight
import io.reactivex.Observable
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.doReturn
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class MainActivityViewModelTest {

    private val initialState = MainActivityView.State()

    private lateinit var testMainActivityViewModel: MainActivityViewModel

    @Mock
    private lateinit var mockObserveThemeChangeTask: ObserveThemeChangeTask

    @Mock
    private lateinit var mockGetThemeModeTask: GetThemeModeTask

    @Before
    fun setup() {
        testMainActivityViewModel =
            MainActivityViewModel(
                state = initialState,
                getThemeModeTask = mockGetThemeModeTask,
                observeThemeChangeTask = mockObserveThemeChangeTask
            )
    }

    @Test
    fun `send initialise event and receive render event none`() {

        doReturn(getResultEither(LIGHT_THEME))
            .`when`(mockObserveThemeChangeTask)
            .invoke()

        MainActivityView
            .Event
            .OnViewInitialised
            .asObservable()
            .compose(testMainActivityViewModel())
            .test()
            .assertNoErrors()
            .assertComplete()
            .assertValueAt(0) { receivedState: MainActivityView.State ->
                receivedState.renderEvent is
                    MainActivityView.RenderEvent.None
            }
    }

    @Test
    fun `send initialise event and receive render event restart activity on non default theme`() {

        doReturn(getResultEither(DARK_THEME))
            .`when`(mockObserveThemeChangeTask)
            .invoke()

        MainActivityView
            .Event
            .OnViewInitialised
            .asObservable()
            .compose(testMainActivityViewModel())
            .test()
            .assertNoErrors()
            .assertComplete()
            .assertValueAt(0) { receivedState: MainActivityView.State ->
                receivedState.renderEvent is
                    MainActivityView.RenderEvent.RestartActivity
            }
    }

    private fun getResultEither(theme: String): Observable<Either<Failure, String>> =
        theme
            .asRight()
            .asObservable()
}