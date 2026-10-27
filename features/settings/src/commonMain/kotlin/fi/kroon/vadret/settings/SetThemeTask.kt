package fi.kroon.vadret.settings

import fi.kroon.vadret.common.Failure
import fi.kroon.vadret.theme.model.Theme
import io.github.sphrak.either.Either

internal interface SetThemeTask {
    suspend operator fun invoke(theme: Theme): Either<Failure, Unit>
}