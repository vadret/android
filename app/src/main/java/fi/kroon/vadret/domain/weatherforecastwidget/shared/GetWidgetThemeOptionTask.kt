package fi.kroon.vadret.domain.weatherforecastwidget.shared

import fi.kroon.vadret.data.exception.Failure
import fi.kroon.vadret.data.functional.Either
import fi.kroon.vadret.data.functional.map
import fi.kroon.vadret.data.weatherforecastwidget.local.WidgetThemeLocalDataSource
import io.reactivex.Single
import timber.log.Timber
import javax.inject.Inject

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