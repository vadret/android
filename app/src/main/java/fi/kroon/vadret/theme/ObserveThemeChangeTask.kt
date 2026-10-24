package fi.kroon.vadret.theme

import fi.kroon.vadret.data.failure.Failure
import fi.kroon.vadret.data.theme.local.ThemeLocalKeyValueDataSource
import fi.kroon.vadret.util.THEME_MODE_KEY
import io.reactivex.Observable
import se.fimbla.either.Either
import javax.inject.Inject

class ObserveThemeChangeTask @Inject constructor(
    private val local: ThemeLocalKeyValueDataSource
) {
    operator fun invoke(): Observable<Either<Failure, String>> = local
        .observeString(THEME_MODE_KEY)
}