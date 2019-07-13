package fi.kroon.vadret.presentation.aboutapp

import io.reactivex.Observable
import org.junit.Before
import org.junit.Test

class AboutAppViewModelTest {

    private lateinit var testAboutAppViewModel: AboutAppViewModel

    private val initialState = AboutAppView.State()

    private val position: Int = 0

    @Before
    fun setup() {
        testAboutAppViewModel = AboutAppViewModel(state = initialState)
    }
    @Test
    fun `send initialise event and receive initialised state`() {

        Observable.just(
            AboutAppView.Event.OnViewInitialised
        ).compose(
            testAboutAppViewModel()
        ).test()
            .assertNoErrors()
            .assertComplete()
            .assertValueAt(0) { viewState: AboutAppView.State ->
                viewState.isInitialised
            }
    }

    @Test
    fun `send tab selected event and receive untouched state `() {
        Observable.just(
            AboutAppView.Event.OnTabSelected(position = position)
        ).compose(
            testAboutAppViewModel()
        ).test()
            .assertComplete()
            .assertNoErrors()
    }
}