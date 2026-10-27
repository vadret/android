package fi.kroon.vadret.settings

import fi.kroon.vadret.common.Failure
import fi.kroon.vadret.language.LanguageRepository
import fi.kroon.vadret.language.model.AppLanguage
import io.github.sphrak.either.Either

internal class DefaultSetLanguageTask(
    private val languageRepository: LanguageRepository
) : SetLanguageTask {

    override suspend fun invoke(language: AppLanguage): Either<Failure, Unit> = languageRepository.setLanguage(language = language)
}