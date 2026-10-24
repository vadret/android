package fi.kroon.vadret.theme

import fi.kroon.vadret.data.failure.Failure
import fi.kroon.vadret.data.theme.exception.ThemeFailure
import fi.kroon.vadret.data.theme.local.ThemeLocalKeyValueDataSource
import fi.kroon.vadret.util.LIGHT_THEME
import fi.kroon.vadret.util.THEME_MODE_KEY
import fi.kroon.vadret.util.extension.asLeft
import fi.kroon.vadret.util.extension.asObservable
import fi.kroon.vadret.util.extension.asRight
import io.reactivex.Observable
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.doReturn
import org.mockito.junit.MockitoJUnitRunner
import se.fimbla.either.Either

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
    fun `successfully retreive theme string`() {

        doReturn(getResultEither())
            .`when`(mockThemeLocalKeyValueDataSource)
            .observeString(THEME_MODE_KEY)

        testObserveThemeChangeTask()
            .test()
            .assertNoErrors()
            .assertComplete()
            .assertValueAt(0) {
                it is Either.Right && it.value == LIGHT_THEME
            }
    }

    @Test
    fun `fail to retreive theme string throw theme not found`() {

        doReturn(getFailureEither())
            .`when`(mockThemeLocalKeyValueDataSource)
            .observeString(THEME_MODE_KEY)

        testObserveThemeChangeTask()
            .test()
            .assertNoErrors()
            .assertComplete()
            .assertValueAt(0) {
                it is Either.Left && it.value is ThemeFailure.ThemeNotFound
            }
    }

    private fun getFailureEither(): Observable<Either<Failure, String>> =
        ThemeFailure
            .ThemeNotFound
            .asLeft()
            .asObservable()

    private fun getResultEither(): Observable<Either<Failure, String>> =
        LIGHT_THEME
            .asRight()
            .asObservable()
}