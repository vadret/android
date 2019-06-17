package fi.kroon.vadret.data.theme.local

import com.afollestad.rxkprefs.Pref
import com.afollestad.rxkprefs.RxkPrefs
import fi.kroon.vadret.data.exception.Failure
import fi.kroon.vadret.data.functional.Either
import fi.kroon.vadret.data.theme.exception.ThemeFailure
import fi.kroon.vadret.util.LIGHT_THEME
import fi.kroon.vadret.util.THEME_MODE_KEY
import fi.kroon.vadret.util.extension.asLeft
import io.reactivex.Observable
import io.reactivex.Single
import javax.inject.Inject

class ThemeLocalKeyValueDataSource @Inject constructor(
    private val rxkPrefs: RxkPrefs
) {
    private val theme: Pref<String> = rxkPrefs.string(THEME_MODE_KEY, LIGHT_THEME)

    fun getString(key: String): Single<Either<Failure, String>> =
        when (key) {
            THEME_MODE_KEY -> Single.just(
                Either.Right(
                    theme.get()
                )
            )
            else -> {
                Single.just(
                    ThemeFailure
                        .ThemeNotFound
                        .asLeft()
                )
            }
        }

    fun observeString(key: String): Observable<Either<Failure, String>> =
        when (key) {
            THEME_MODE_KEY -> theme.observe()
            else -> {
                throw Error("key doesn't exist")
            }
        }.map { value: String ->
            Either.Right(value) as Either<Failure, String>
        }.onErrorReturn {
            ThemeFailure
                .ThemeNotFound
                .asLeft()
        }
}