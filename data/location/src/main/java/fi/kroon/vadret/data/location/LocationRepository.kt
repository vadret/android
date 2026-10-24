package fi.kroon.vadret.data.location

import fi.kroon.vadret.data.failure.Failure
import fi.kroon.vadret.data.location.exception.LocationFailure
import fi.kroon.vadret.data.location.local.LocationLocalDataSource
import fi.kroon.vadret.data.location.model.Location
import fi.kroon.vadret.util.extension.asLeft
import io.github.sphrak.either.Either
import timber.log.Timber
import javax.inject.Inject

class LocationRepository @Inject constructor(
    private val locationLocalDataSource: LocationLocalDataSource
) {
    operator fun invoke(): Either<Failure, Location> =
        try {
            locationLocalDataSource()
        } catch (exception: Exception) {
            Timber.e("LocationRepositoryFailure: $exception")
            LocationFailure
                .LocationNotAvailable
                .asLeft()
        }
}