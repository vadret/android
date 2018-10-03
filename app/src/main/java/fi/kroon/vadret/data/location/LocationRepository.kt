package fi.kroon.vadret.data.location

import fi.kroon.vadret.data.exception.Either
import fi.kroon.vadret.data.exception.Failure
import fi.kroon.vadret.data.location.exception.LocationFailure
import fi.kroon.vadret.data.location.model.Location
import io.reactivex.Single
import timber.log.Timber
import javax.inject.Inject

class LocationRepository @Inject constructor(
    private val locationProvider: LocationProvider
) {
    fun get(): Single<Either<Failure, Location>> {
        return Single.just(
            locationProvider.get()
        ).doOnEvent { t1, t2 ->
            Timber.d("T1: $t1, T2: $t2")
        }.doOnError {
            Timber.e("$it")
        }.onErrorReturn {
            Either.Left(LocationFailure.LocationNotAvailableFailure())
        }
    }
}