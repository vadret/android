package fi.kroon.vadret.settings

import fi.kroon.vadret.common.Failure
import fi.kroon.vadret.language.model.AppLanguage
import io.github.sphrak.either.Either

internal interface SetLanguageTask {
    suspend operator fun invoke(language: AppLanguage): Either<Failure, Unit>
}