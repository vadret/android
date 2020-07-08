package fi.kroon.vadret.domain.weatherforecastwidget.shared

import fi.kroon.vadret.data.failure.Failure
import fi.kroon.vadret.data.weatherforecastwidget.local.WidgetUpdateIntervalLocalDataSource
import io.github.sphrak.either.Either
import io.github.sphrak.either.map
import io.reactivex.Single
import timber.log.Timber
import javax.inject.Inject

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