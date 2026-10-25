package fi.kroon.vadret.main

import fi.kroon.vadret.common.Failure
import fi.kroon.vadret.theme.ThemeRepository
import fi.kroon.vadret.theme.model.Theme
import io.github.sphrak.either.Either
import kotlinx.coroutines.flow.Flow

internal class DefaultObserveThemeTask(
    private val themeRepository: ThemeRepository
) : ObserveThemeTask {

    override fun invoke(): Flow<Either<Failure, Theme>> = themeRepository.observeTheme()
}