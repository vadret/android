package fi.kroon.vadret.theme

import fi.kroon.vadret.data.failure.Failure
import fi.kroon.vadret.data.theme.exception.ThemeFailure
import fi.kroon.vadret.data.theme.local.ThemeLocalKeyValueDataSource
import fi.kroon.vadret.util.LIGHT_THEME
import fi.kroon.vadret.util.THEME_MODE_KEY
import fi.kroon.vadret.util.extension.asLeft
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
class ObserveThemeChangeTaskTest {

    private lateinit var testObserveThemeChangeTask: ObserveThemeChangeTask

    @Mock
    private lateinit var mockThemeLocalKeyValueDataSource: ThemeLocalKeyValueDataSource

    @Before
    fun setup() {
        testObserveThemeChangeTask = ObserveThemeChangeTask(mockThemeLocalKeyValueDataSource)
    }

    @Test
    fun `successfully retreive theme string`() = runTest {

        doReturn(getResultEither())
            .`when`(mockThemeLocalKeyValueDataSource)
            .observeString(THEME_MODE_KEY)

        assertTrue(
            testObserveThemeChangeTask()
                .first()
                .let {
                    it is Either.Right && it.b == LIGHT_THEME
                }
        )
    }

    @Test
    fun `fail to retreive theme string throw theme not found`() = runTest {

        doReturn(getFailureEither())
            .`when`(mockThemeLocalKeyValueDataSource)
            .observeString(THEME_MODE_KEY)

        assertTrue(
            testObserveThemeChangeTask()
                .first()
                .let {
                    it is Either.Left && it.a is ThemeFailure.ThemeNotFound
                }
        )
    }

    private fun getFailureEither(): Flow<Either<Failure, String>> =
        flowOf(
            ThemeFailure
                .ThemeNotFound
                .asLeft()
        )

    private fun getResultEither(): Flow<Either<Failure, String>> =
        flowOf(
            LIGHT_THEME
                .asRight()
        )
}