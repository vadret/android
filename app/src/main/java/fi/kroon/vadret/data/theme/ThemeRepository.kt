package fi.kroon.vadret.data.theme

import fi.kroon.vadret.R
import fi.kroon.vadret.data.failure.Failure
import fi.kroon.vadret.data.theme.exception.ThemeFailure
import fi.kroon.vadret.data.theme.local.ThemeLocalKeyValueDataSource
import fi.kroon.vadret.data.theme.model.Theme
import fi.kroon.vadret.util.AMOLED_THEME
import fi.kroon.vadret.util.DARK_THEME
import fi.kroon.vadret.util.extension.asLeft
import fi.kroon.vadret.util.extension.asRight
import io.github.sphrak.either.Either
import io.reactivex.Single
import javax.inject.Inject
import timber.log.Timber

class ThemeRepository @Inject constructor(
    private val keyValueStore: ThemeLocalKeyValueDataSource
) {

    operator fun invoke(key: String): Single<Either<Failure, Theme>> =
        keyValueStore.getString(key)
            .map { result: Either<Failure, String> ->
                result.either(
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
                                    resourceId = R.style.DARK
                                ).asRight()
                            }
                            AMOLED_THEME -> {
                                Theme(
                                    name = theme,
                                    resourceId = R.style.AMOLED
                                ).asRight()
                            }
                            else -> {
                                Theme(
                                    name = theme,
                                    resourceId = R.style.LIGHT
                                ).asRight()
                            }
                        }
                    }
                )
            }
}