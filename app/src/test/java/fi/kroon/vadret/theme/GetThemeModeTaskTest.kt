package fi.kroon.vadret.theme

import fi.kroon.vadret.core.R
import fi.kroon.vadret.data.failure.Failure
import fi.kroon.vadret.data.theme.ThemeRepository
import fi.kroon.vadret.data.theme.exception.ThemeFailure
import fi.kroon.vadret.data.theme.model.Theme
import fi.kroon.vadret.util.LIGHT_THEME
import fi.kroon.vadret.util.THEME_MODE_KEY
import fi.kroon.vadret.util.extension.asLeft
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
class GetThemeModeTaskTest {

    private val theme = Theme(
        name = LIGHT_THEME,
        resourceId = R.style.LIGHT
    )

    @Mock
    private lateinit var mockThemeRepository: ThemeRepository

    private lateinit var testGetThemeModeTask: GetThemeModeTask

    @Before
    fun setup() {
        testGetThemeModeTask = GetThemeModeTask(repo = mockThemeRepository)
    }

    @Test
    fun `fetch theme successfully`() = runTest {

        doReturn(getResultEither())
            .`when`(mockThemeRepository)
            .invoke(key = THEME_MODE_KEY)

        assertTrue(
            testGetThemeModeTask()
                .let {
                    it is Either.Right && it.b == theme
                }
        )
    }

    @Test
    fun `fail to fetch theme`() = runTest {
        doReturn(getFailureEither())
            .`when`(mockThemeRepository)
            .invoke(key = THEME_MODE_KEY)

        assertTrue(
            testGetThemeModeTask()
                .let {
                    it is Either.Left && it.a is ThemeFailure.ParsingThemeFailed
                }
        )
    }

    private fun getFailureEither(): Either<Failure, Theme> =
        ThemeFailure
            .ParsingThemeFailed
            .asLeft()

    private fun getResultEither(): Either<Failure, Theme> =
        theme
            .asRight()
}