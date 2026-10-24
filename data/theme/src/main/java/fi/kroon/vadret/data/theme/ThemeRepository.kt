package fi.kroon.vadret.data.theme

import fi.kroon.vadret.data.failure.Failure
import fi.kroon.vadret.data.theme.exception.ThemeFailure
import fi.kroon.vadret.data.theme.local.ThemeLocalKeyValueDataSource
import fi.kroon.vadret.data.theme.model.Theme
import fi.kroon.vadret.data.theme.model.ThemeStyles
import fi.kroon.vadret.util.AMOLED_THEME
import fi.kroon.vadret.util.DARK_THEME
import fi.kroon.vadret.util.extension.asLeft
import fi.kroon.vadret.util.extension.asRight
import io.github.sphrak.either.Either
import io.github.sphrak.either.extension.onResult
import timber.log.Timber
import javax.inject.Inject

class ThemeRepository @Inject constructor(
    private val keyValueStore: ThemeLocalKeyValueDataSource,
    private val themeStyles: ThemeStyles
) {

    operator fun invoke(key: String): Either<Failure, Theme> =
        keyValueStore.getString(key)
            .let { result: Either<Failure, String> ->
                result.onResult(
                    { failure: Failure ->
                        Timber.e("Failure: $failure")
                        ThemeFailure
                            .ParsingThemeFailed
                            .asLeft()
                    },
                    { theme: String ->
                        when (theme) {
                            DARK_THEME -> {
                                Theme(
                                    name = theme,
                                    resourceId = themeStyles.dark
                                ).asRight()
                            }
                            AMOLED_THEME -> {
                                Theme(
                                    name = theme,
                                    resourceId = themeStyles.amoled
                                ).asRight()
                            }
                            else -> {
                                Theme(
                                    name = theme,
                                    resourceId = themeStyles.light
                                ).asRight()
                            }
                        }
                    }
                )
            }
}