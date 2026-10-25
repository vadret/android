package fi.kroon.vadret.settings

import fi.kroon.vadret.common.Failure
import fi.kroon.vadret.theme.ThemeRepository
import fi.kroon.vadret.theme.model.Theme
import io.github.sphrak.either.Either

internal class DefaultSetThemeTask(
    private val themeRepository: ThemeRepository
) : SetThemeTask {

    override suspend fun invoke(theme: Theme): Either<Failure, Unit> = themeRepository.setTheme(theme = theme)
}