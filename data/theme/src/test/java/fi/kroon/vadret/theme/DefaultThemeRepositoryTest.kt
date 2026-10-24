package fi.kroon.vadret.theme

import fi.kroon.vadret.common.ErrorCode
import fi.kroon.vadret.common.Failure
import fi.kroon.vadret.logging.LogLevel
import fi.kroon.vadret.testing.FakeSharedPreferences
import fi.kroon.vadret.testing.LogLine
import fi.kroon.vadret.testing.withRecordedLogs
import fi.kroon.vadret.theme.local.DefaultThemeLocalDataSource
import fi.kroon.vadret.theme.local.ThemeLocalDataSource
import fi.kroon.vadret.theme.model.Theme
import io.github.sphrak.either.Either
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import java.io.IOException
import kotlin.test.Test
import kotlin.test.assertEquals

private const val THEME_KEY = "APP_THEME_MODE"

private class BrokenThemeLocalDataSource : ThemeLocalDataSource {
    override suspend fun getTheme(): String? = throw IOException("disk full")
    override fun observeTheme(): Flow<String?> = flow { throw IOException("disk full") }
    override suspend fun setTheme(value: String) = throw IOException("disk full")
}

class DefaultThemeRepositoryTest {

    private val testScope = TestScope(StandardTestDispatcher())
    private val sharedPreferences = FakeSharedPreferences()
    private val themeRepository: ThemeRepository = DefaultThemeRepository(
        themeLocalDataSource = DefaultThemeLocalDataSource(
            sharedPreferences = sharedPreferences,
            coroutineDispatcher = StandardTestDispatcher(testScope.testScheduler)
        )
    )

    @Test
    fun `nothing stored is the light theme`() = testScope.runTest {
        assertEquals(expected = Either.Right(b = Theme.LIGHT), actual = themeRepository.getTheme())
    }

    @Test
    fun `values written by the old preference screen are read`() = testScope.runTest {
        val expected: Map<String, Theme> = mapOf(
            "Light" to Theme.LIGHT,
            "Light (No background)" to Theme.LIGHT,
            "Dark" to Theme.DARK,
            "AMOLED" to Theme.AMOLED
        )

        val actual: Map<String, Either<Failure, Theme>> = expected.keys.associateWith { value: String ->
            sharedPreferences.edit().putString(THEME_KEY, value).apply()
            themeRepository.getTheme()
        }

        assertEquals(
            expected = expected.mapValues { (_, theme: Theme) -> Either.Right(b = theme) },
            actual = actual
        )
    }

    @Test
    fun `a set theme is stored as the old preference value`() = testScope.runTest {
        val stored: List<String?> = Theme.entries.map { theme: Theme ->
            themeRepository.setTheme(theme = theme)
            sharedPreferences.getString(THEME_KEY, null)
        }

        assertEquals(expected = listOf("Light", "Dark", "AMOLED"), actual = stored)
    }

    @Test
    fun `observing emits the stored theme and then each change`() = testScope.runTest {
        val emitted = mutableListOf<Either<Failure, Theme>>()
        val job = launch {
            themeRepository.observeTheme().take(count = 3).toList(destination = emitted)
        }
        advanceUntilIdle()

        themeRepository.setTheme(theme = Theme.DARK)
        advanceUntilIdle()
        themeRepository.setTheme(theme = Theme.AMOLED)
        advanceUntilIdle()
        job.join()

        assertEquals<List<Either<Failure, Theme>>>(
            expected = listOf(Either.Right(b = Theme.LIGHT), Either.Right(b = Theme.DARK), Either.Right(b = Theme.AMOLED)),
            actual = emitted
        )
        assertEquals(expected = 0, actual = sharedPreferences.listenerCount)
    }

    @Test
    fun `a failing store is an unknown failure and logged as an error`() = testScope.runTest {
        val brokenRepository: ThemeRepository = DefaultThemeRepository(themeLocalDataSource = BrokenThemeLocalDataSource())

        withRecordedLogs { recordingLogSink ->
            val results: List<Either<Failure, Any>> = listOf(
                brokenRepository.getTheme(),
                brokenRepository.setTheme(theme = Theme.DARK),
                brokenRepository.observeTheme().toList().single()
            )

            assertEquals(
                expected = List(size = 3) { Either.Left(a = Failure(errorCode = ErrorCode.UNKNOWN)) },
                actual = results
            )
            assertEquals(
                expected = List(size = 3) { "fi.kroon.vadret.theme" to LogLevel.ERROR },
                actual = recordingLogSink.lines.map { line: LogLine -> line.tag to line.level }
            )
        }
    }
}