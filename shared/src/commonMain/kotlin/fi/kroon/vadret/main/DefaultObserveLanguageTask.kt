package fi.kroon.vadret.main

import fi.kroon.vadret.common.Failure
import fi.kroon.vadret.language.LanguageRepository
import fi.kroon.vadret.language.model.AppLanguage
import io.github.sphrak.either.Either
import kotlinx.coroutines.flow.Flow

internal class DefaultObserveLanguageTask(
    private val languageRepository: LanguageRepository
) : ObserveLanguageTask {

    override fun invoke(): Flow<Either<Failure, AppLanguage>> = languageRepository.observeLanguage()
}