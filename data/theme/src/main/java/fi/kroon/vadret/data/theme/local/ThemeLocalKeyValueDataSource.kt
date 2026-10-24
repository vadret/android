package fi.kroon.vadret.data.theme.local

import fi.kroon.vadret.data.failure.Failure
import fi.kroon.vadret.data.preference.Pref
import fi.kroon.vadret.data.preference.Prefs
import fi.kroon.vadret.data.theme.exception.ThemeFailure
import fi.kroon.vadret.util.LIGHT_THEME
import fi.kroon.vadret.util.THEME_MODE_KEY
import fi.kroon.vadret.util.extension.asLeft
import fi.kroon.vadret.util.extension.asRight
import io.github.sphrak.either.Either
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class ThemeLocalKeyValueDataSource @Inject constructor(
    private val prefs: Prefs
) {

    private val theme: Pref<String> = prefs.string(THEME_MODE_KEY, LIGHT_THEME)

    fun getString(key: String): Either<Failure, String> =
        when (key) {
            THEME_MODE_KEY ->
                theme
                    .get()
                    .asRight()
            else -> {
                ThemeFailure
                    .ThemeNotFound
                    .asLeft()
            }
        }

    fun putString(
        key: String,
        value: String
    ): Either<Failure, Unit> =
        when (key) {
            THEME_MODE_KEY ->
                theme
                    .set(value = value)
                    .asRight()
            else -> {
                ThemeFailure
                    .ThemeNotFound
                    .asLeft()
            }
        }

    fun observeString(key: String): Flow<Either<Failure, String>> =
        when (key) {
            THEME_MODE_KEY ->
                theme
                    .observe()
                    .map { value: String ->
                        value.asRight()
                    }
            else -> flowOf(
                ThemeFailure
                    .ThemeNotFound
                    .asLeft()
            )
        }
}