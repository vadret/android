package fi.kroon.vadret.domain.theme

import fi.kroon.vadret.data.failure.Failure
import fi.kroon.vadret.data.theme.ThemeRepository
import fi.kroon.vadret.data.theme.model.Theme
import fi.kroon.vadret.util.THEME_MODE_KEY
import io.reactivex.Single
import se.fimbla.either.Either
import javax.inject.Inject

class GetThemeModeTask @Inject constructor(
    private val repo: ThemeRepository
) {
    operator fun invoke(): Single<Either<Failure, Theme>> =
        repo(THEME_MODE_KEY)
}