package fi.kroon.vadret.domain.theme

import fi.kroon.vadret.R
import fi.kroon.vadret.data.failure.Failure
import fi.kroon.vadret.data.theme.ThemeRepository
import fi.kroon.vadret.data.theme.exception.ThemeFailure
import fi.kroon.vadret.data.theme.model.Theme
import fi.kroon.vadret.util.LIGHT_THEME
import fi.kroon.vadret.util.THEME_MODE_KEY
import fi.kroon.vadret.util.extension.asLeft
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
    fun `fetch theme successfully`() {

        doReturn(getResultEither())
            .`when`(mockThemeRepository)
            .invoke(key = THEME_MODE_KEY)

        testGetThemeModeTask()
            .test()
            .assertNoErrors()
            .assertComplete()
            .assertValueAt(0) {
                it is Either.Right && it.b == theme
            }
    }

    @Test
    fun `fail to fetch theme`() {
        doReturn(getFailureEither())
            .`when`(mockThemeRepository)
            .invoke(key = THEME_MODE_KEY)

        testGetThemeModeTask()
            .test()
            .assertNoErrors()
            .assertComplete()
            .assertValueAt(0) {
                it is Either.Left && it.a is ThemeFailure.ParsingThemeFailed
            }
    }

    private fun getFailureEither(): Single<Either<Failure, Theme>> =
        ThemeFailure
            .ParsingThemeFailed
            .asLeft()
            .asSingle()

    private fun getResultEither(): Single<Either<Failure, Theme>> =
        theme
            .asRight()
            .asSingle()
}