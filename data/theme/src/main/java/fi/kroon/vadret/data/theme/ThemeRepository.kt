package fi.kroon.vadret.data.theme

import fi.kroon.vadret.data.failure.Failure
import fi.kroon.vadret.data.theme.extension.toPreferenceValue
import fi.kroon.vadret.data.theme.extension.toTheme
import fi.kroon.vadret.data.theme.local.ThemeLocalKeyValueDataSource
import fi.kroon.vadret.data.theme.model.Theme
import fi.kroon.vadret.util.THEME_MODE_KEY
import io.github.sphrak.either.Either
import io.github.sphrak.either.map
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class ThemeRepository @Inject constructor(
    private val themeLocalKeyValueDataSource: ThemeLocalKeyValueDataSource
) {

    fun get(): Either<Failure, Theme> =
        themeLocalKeyValueDataSource
            .getString(key = THEME_MODE_KEY)
            .map { value: String -> value.toTheme() }

    fun observe(): Flow<Either<Failure, Theme>> =
        themeLocalKeyValueDataSource
            .observeString(key = THEME_MODE_KEY)
            .map { result: Either<Failure, String> ->
                result.map { value: String -> value.toTheme() }
            }

    fun set(theme: Theme): Either<Failure, Unit> =
        themeLocalKeyValueDataSource.putString(
            key = THEME_MODE_KEY,
            value = theme.toPreferenceValue()
        )
}