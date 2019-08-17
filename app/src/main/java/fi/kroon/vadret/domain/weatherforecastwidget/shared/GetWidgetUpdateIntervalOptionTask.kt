package fi.kroon.vadret.domain.weatherforecastwidget.shared

import fi.kroon.vadret.data.exception.Failure
import fi.kroon.vadret.data.functional.Either
import fi.kroon.vadret.data.functional.map
import fi.kroon.vadret.data.weatherforecastwidget.local.WidgetUpdateIntervalLocalDataSource
import io.reactivex.Single
import javax.inject.Inject
import timber.log.Timber

class GetWidgetUpdateIntervalOptionTask @Inject constructor(
    private val local: WidgetUpdateIntervalLocalDataSource
) {
    operator fun invoke(position: Int): Single<Either<Failure, String>> =
        local()
            .map { result: Either<Failure, Array<String>> ->
                result.map { array: Array<String> ->
                    Timber.i("GetWidgetUpdateIntervalOptionTask: $position")
                    array.get(position)
                }
            }
}