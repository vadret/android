package fi.kroon.vadret.domain.weatherforecast

import fi.kroon.vadret.data.autocomplete.model.AutoCompleteItem
import fi.kroon.vadret.data.exception.Failure
import fi.kroon.vadret.data.functional.Either
import fi.kroon.vadret.data.weatherforecast.local.WeatherForecastLocalKeyValueDataSource
import fi.kroon.vadret.utils.COUNTY_KEY
import fi.kroon.vadret.utils.LATITUDE_KEY
import fi.kroon.vadret.utils.LOCALITY_KEY
import fi.kroon.vadret.utils.LONGITUDE_KEY
import fi.kroon.vadret.utils.MUNICIPALITY_KEY
import io.reactivex.Observable
import io.reactivex.Single
import timber.log.Timber
import javax.inject.Inject

class SetLocationInformationTask @Inject constructor(
    private val repo: WeatherForecastLocalKeyValueDataSource
) {

    /**
     *  Merge observable sources and get a combined list of Either's
     *  back as result
     */
    operator fun invoke(autoCompleteItem: AutoCompleteItem): Single<Either<Failure, Unit>> =
        Observable.mergeArray(
            repo.putString(LATITUDE_KEY, autoCompleteItem.latitude.toString()).toObservable(),
            repo.putString(LONGITUDE_KEY, autoCompleteItem.longitude.toString()).toObservable(),
            repo.putString(LOCALITY_KEY, autoCompleteItem.locality).toObservable(),
            repo.putString(MUNICIPALITY_KEY, autoCompleteItem.municipality).toObservable(),
            repo.putString(COUNTY_KEY, autoCompleteItem.county).toObservable()
        ).toList()
            .flatMapObservable { eitherList: List<Either<Failure, Unit>> ->

                /**
                 *  [0] + [1]
                 *  [0||1] + [2]
                 *  [0||1||2] + [3]
                 */

                /**
                 * @flatMapObservable to flatten the stream
                 *
                 * Every item in list becomes an observable
                 * so we can use the operators (scan)?
                 *
                 * Then we scan, and if its .isLeft
                 */
                Observable.fromIterable(eitherList)
                    .scan { previous: Either<Failure, Unit>, next: Either<Failure, Unit> ->
                        Timber.d("SetLocationInformationTask: $eitherList")
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