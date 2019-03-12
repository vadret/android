package fi.kroon.vadret.domain.weatherforecast

import fi.kroon.vadret.data.exception.Failure
import fi.kroon.vadret.data.functional.Either
import fi.kroon.vadret.data.weatherforecast.local.WeatherForecastLocalKeyValueDataSource
import fi.kroon.vadret.utils.AUTOMATIC_LOCATION_MODE_KEY
import fi.kroon.vadret.utils.COUNTY_KEY
import fi.kroon.vadret.utils.DEFAULT_COUNTY
import fi.kroon.vadret.utils.DEFAULT_LATITUDE
import fi.kroon.vadret.utils.DEFAULT_LOCALITY
import fi.kroon.vadret.utils.DEFAULT_LONGITUDE
import fi.kroon.vadret.utils.DEFAULT_MUNICIPALITY
import fi.kroon.vadret.utils.LATITUDE_KEY
import fi.kroon.vadret.utils.LOCALITY_KEY
import fi.kroon.vadret.utils.LONGITUDE_KEY
import fi.kroon.vadret.utils.MUNICIPALITY_KEY
import io.reactivex.Observable
import io.reactivex.Single
import timber.log.Timber
import javax.inject.Inject

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