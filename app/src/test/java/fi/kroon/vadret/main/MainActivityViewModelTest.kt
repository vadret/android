package fi.kroon.vadret.main

import fi.kroon.vadret.data.failure.Failure
import fi.kroon.vadret.theme.GetThemeModeTask
import fi.kroon.vadret.theme.ObserveThemeChangeTask
import fi.kroon.vadret.util.DARK_THEME
import fi.kroon.vadret.util.LIGHT_THEME
import fi.kroon.vadret.util.extension.asRight
import io.github.sphrak.either.Either
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertTrue
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
    fun `send initialise event and receive render event none`() = runTest {

        doReturn(getResultEither(LIGHT_THEME))
            .`when`(mockObserveThemeChangeTask)
            .invoke()

        assertTrue(
            testMainActivityViewModel
                .viewState
                .first()
                .let { receivedState: MainActivityView.State ->
                    receivedState.renderEvent is
                    MainActivityView.RenderEvent.None
                }
        )
    }

    @Test
    fun `send initialise event and receive render event restart activity on non default theme`() = runTest {

        doReturn(getResultEither(DARK_THEME))
            .`when`(mockObserveThemeChangeTask)
            .invoke()

        assertTrue(
            testMainActivityViewModel
                .viewState
                .first()
                .let { receivedState: MainActivityView.State ->
                    receivedState.renderEvent is
                    MainActivityView.RenderEvent.RestartActivity
                }
        )
    }

    private fun getResultEither(theme: String): Flow<Either<Failure, String>> =
        flowOf(
            theme
                .asRight()
        )
}