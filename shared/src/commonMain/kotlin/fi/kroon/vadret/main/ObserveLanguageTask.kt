package fi.kroon.vadret.main

import fi.kroon.vadret.common.Failure
import fi.kroon.vadret.language.model.AppLanguage
import io.github.sphrak.either.Either
import kotlinx.coroutines.flow.Flow

internal interface ObserveLanguageTask {

    operator fun invoke(): Flow<Either<Failure, AppLanguage>>
}