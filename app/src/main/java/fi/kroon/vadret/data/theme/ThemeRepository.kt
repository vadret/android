package fi.kroon.vadret.data.theme

import fi.kroon.vadret.R
import fi.kroon.vadret.data.exception.Failure
import fi.kroon.vadret.data.functional.Either
import fi.kroon.vadret.data.theme.exception.ThemeFailure
import fi.kroon.vadret.data.theme.local.ThemeLocalKeyValueDataSource
import fi.kroon.vadret.data.theme.model.Theme
import fi.kroon.vadret.utils.AMOLED_THEME
import fi.kroon.vadret.utils.DARK_THEME
import fi.kroon.vadret.utils.extensions.asLeft
import fi.kroon.vadret.utils.extensions.asRight
import io.reactivex.Single
import timber.log.Timber
import javax.inject.Inject

class ThemeRepository @Inject constructor(
    private val keyValueStore: ThemeLocalKeyValueDataSource
) {

    operator fun invoke(key: String): Single<Either<Failure, Theme>> =
        keyValueStore.getString(key)
            .map { result ->
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