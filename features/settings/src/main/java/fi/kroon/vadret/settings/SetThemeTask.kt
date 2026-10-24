package fi.kroon.vadret.settings

import fi.kroon.vadret.data.failure.Failure
import fi.kroon.vadret.data.theme.ThemeRepository
import fi.kroon.vadret.data.theme.model.Theme
import io.github.sphrak.either.Either
import javax.inject.Inject

class SetThemeTask @Inject constructor(
    private val themeRepository: ThemeRepository
) {

    operator fun invoke(theme: Theme): Either<Failure, Unit> =
        themeRepository.set(theme = theme)
}