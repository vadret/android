package fi.kroon.vadret.main

import fi.kroon.vadret.common.ErrorCode
import fi.kroon.vadret.common.Failure
import fi.kroon.vadret.theme.model.Theme
import io.github.sphrak.either.Either
import io.github.sphrak.either.extension.asLeft
import io.github.sphrak.either.extension.asRight
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

private class FakeObserveThemeTask : ObserveThemeTask {

    val themes = MutableSharedFlow<Either<Failure, Theme>>()

    override fun invoke(): Flow<Either<Failure, Theme>> = themes
}

@OptIn(ExperimentalCoroutinesApi::class)
class MainViewModelTest {

    private val testDispatcher: TestDispatcher = StandardTestDispatcher()
    private val testScope = TestScope(testDispatcher)

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(dispatcher = testDispatcher)
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `the theme is null until read and then follows every change`() =
        testScope.runTest {
            val observeThemeTask = FakeObserveThemeTask()
            val mainViewModel = MainViewModel(
                observeThemeTask = observeThemeTask,
                coroutineDispatcher = testDispatcher
            )
            val seen = mutableListOf<Theme?>()
            val job = launch { mainViewModel.theme.collect { theme: Theme? -> seen.add(theme) } }
            advanceUntilIdle()

            observeThemeTask.themes.emit(value = Theme.DARK.asRight())
            advanceUntilIdle()
            observeThemeTask.themes.emit(value = Failure(errorCode = ErrorCode.UNKNOWN).asLeft())
            advanceUntilIdle()
            job.cancel()

            assertEquals(expected = listOf(null, Theme.DARK, Theme.LIGHT), actual = seen)
        }
}