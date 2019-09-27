package fi.kroon.vadret.data.location

import fi.kroon.vadret.data.failure.Failure
import fi.kroon.vadret.data.location.exception.LocationFailure
import fi.kroon.vadret.data.location.local.LocationLocalDataSource
import fi.kroon.vadret.data.location.model.Location
import fi.kroon.vadret.util.extension.asLeft
import io.github.sphrak.either.Either
import io.reactivex.Single
import javax.inject.Inject
import timber.log.Timber

class LocationRepository @Inject constructor(
    private val locationLocalDataSource: LocationLocalDataSource
) {
    operator fun invoke(): Single<Either<Failure, Location>> =
        Single.fromCallable {
            locationLocalDataSource()
        }.doOnError {
            Timber.e("LocationRepositoryFailure: $it")
        }.onErrorReturn {
            LocationFailure
                .LocationNotAvailable
                .asLeft()
        }
}