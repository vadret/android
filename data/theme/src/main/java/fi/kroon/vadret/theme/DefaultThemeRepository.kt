package fi.kroon.vadret.theme

import fi.kroon.vadret.common.Failure
import fi.kroon.vadret.common.catch
import fi.kroon.vadret.common.either
import fi.kroon.vadret.theme.extension.toFailure
import fi.kroon.vadret.theme.extension.toPreferenceValue
import fi.kroon.vadret.theme.extension.toTheme
import fi.kroon.vadret.theme.local.ThemeLocalDataSource
import fi.kroon.vadret.theme.model.Theme
import io.github.sphrak.either.Either
import io.github.sphrak.either.extension.asLeft
import io.github.sphrak.either.extension.asRight
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map

internal class DefaultThemeRepository(
    private val themeLocalDataSource: ThemeLocalDataSource
) : ThemeRepository {

    override suspend fun getTheme(): Either<Failure, Theme> =
        either { themeLocalDataSource.getTheme().toTheme() }
            .catch { exception: Exception -> exception.toFailure() }

    override fun observeTheme(): Flow<Either<Failure, Theme>> =
        themeLocalDataSource
            .observeTheme()
            .map<String?, Either<Failure, Theme>> { value: String? -> value.toTheme().asRight() }
            .catch { throwable: Throwable ->
                if (throwable is Exception) {
                    emit(throwable.toFailure().asLeft())
                } else {
                    throw throwable
                }
            }

    override suspend fun setTheme(theme: Theme): Either<Failure, Unit> =
        either { themeLocalDataSource.setTheme(value = theme.toPreferenceValue()) }
            .catch { exception: Exception -> exception.toFailure() }
}