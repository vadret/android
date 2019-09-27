package fi.kroon.vadret.domain.weatherforecast

import fi.kroon.vadret.data.failure.Failure
import fi.kroon.vadret.data.weatherforecast.local.WeatherForecastLocalKeyValueDataSource
import fi.kroon.vadret.util.AUTOMATIC_LOCATION_MODE_KEY
import fi.kroon.vadret.util.COUNTY_KEY
import fi.kroon.vadret.util.DEFAULT_COUNTY
import fi.kroon.vadret.util.DEFAULT_LATITUDE
import fi.kroon.vadret.util.DEFAULT_LOCALITY
import fi.kroon.vadret.util.DEFAULT_LONGITUDE
import fi.kroon.vadret.util.DEFAULT_MUNICIPALITY
import fi.kroon.vadret.util.LATITUDE_KEY
import fi.kroon.vadret.util.LOCALITY_KEY
import fi.kroon.vadret.util.LONGITUDE_KEY
import fi.kroon.vadret.util.MUNICIPALITY_KEY
import io.github.sphrak.either.Either
import io.reactivex.Observable
import io.reactivex.Single
import javax.inject.Inject
import timber.log.Timber

class SetDefaultLocationInformationTask @Inject constructor(
    private val repo: WeatherForecastLocalKeyValueDataSource
) {
    operator fun invoke(): Single<Either<Failure, Unit>> =
        Observable.mergeArray(
            repo.putString(LATITUDE_KEY, DEFAULT_LATITUDE).toObservable(),
            repo.putString(LONGITUDE_KEY, DEFAULT_LONGITUDE).toObservable(),
            repo.putString(LOCALITY_KEY, DEFAULT_LOCALITY).toObservable(),
            repo.putString(MUNICIPALITY_KEY, DEFAULT_MUNICIPALITY).toObservable(),
            repo.putString(COUNTY_KEY, DEFAULT_COUNTY).toObservable(),
            repo.putBoolean(AUTOMATIC_LOCATION_MODE_KEY, false).toObservable()
        ).toList()
            .flatMapObservable { eitherList: List<Either<Failure, Unit>> ->
                Observable.fromIterable(eitherList)
                    .scan { previous: Either<Failure, Unit>, next: Either<Failure, Unit> ->
                        Timber.d("Previous: $previous, Next: $next")

                        when {
                            previous.isLeft -> previous
                            next.isLeft -> next
                            else -> next
                        }
                    }
                    .takeLast(1)
            }.singleOrError()
}