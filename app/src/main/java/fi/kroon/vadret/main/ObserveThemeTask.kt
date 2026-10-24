package fi.kroon.vadret.main

import fi.kroon.vadret.common.Failure
import fi.kroon.vadret.theme.model.Theme
import io.github.sphrak.either.Either
import kotlinx.coroutines.flow.Flow

internal interface ObserveThemeTask {

    /**
     *  The theme chosen in settings, then every change to it.
     */
    operator fun invoke(): Flow<Either<Failure, Theme>>
}