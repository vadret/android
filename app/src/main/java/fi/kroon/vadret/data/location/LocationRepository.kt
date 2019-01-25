package fi.kroon.vadret.data.location

import fi.kroon.vadret.data.exception.Failure
import fi.kroon.vadret.data.functional.Either
import fi.kroon.vadret.data.location.exception.LocationFailure
import fi.kroon.vadret.data.location.local.LocationLocalDataSource
import fi.kroon.vadret.data.location.model.Location
import fi.kroon.vadret.utils.extensions.asLeft
import io.reactivex.Single
import timber.log.Timber
import javax.inject.Inject

class LocationRepository @Inject constructor(
    private val locationLocalDataSource: LocationLocalDataSource
) {
    operator fun invoke(): Single<Either<Failure, Location>> =
        Single.fromCallable {
            locationLocalDataSource()
        }.doOnError {
            Timber.e("LocationRepositoryFailure: $it")
        }.onErrorReturn {
            LocationFailure.LocationNotAvailable().asLeft()
        }
}