package fi.kroon.vadret.domain.weatherforecastwidget.shared

import fi.kroon.vadret.data.exception.Failure
import fi.kroon.vadret.data.weatherforecastwidget.local.WidgetThemeLocalDataSource
import io.github.sphrak.either.Either
import io.github.sphrak.either.map
import io.reactivex.Single
import javax.inject.Inject
import timber.log.Timber

class GetWidgetThemeOptionTask @Inject constructor(
    private val local: WidgetThemeLocalDataSource
) {
    operator fun invoke(position: Int): Single<Either<Failure, String>> =
        local()
            .map { result: Either<Failure, Array<String>> ->
                result.map { array: Array<String> ->
                    Timber.i("GetWidgetThemeOptionTask: $position")
                    array.get(position)
                }
            }
}