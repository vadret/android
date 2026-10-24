package fi.kroon.vadret.weatherforecast

import fi.kroon.vadret.data.failure.Failure
import fi.kroon.vadret.data.location.LocationRepository
import fi.kroon.vadret.data.location.model.Location
import io.reactivex.Single
import se.fimbla.either.Either
import timber.log.Timber
import javax.inject.Inject

class GetLocationAutomaticTask @Inject constructor(
    private val locationRepository: LocationRepository
) {
    operator fun invoke(): Single<Either<Failure, Location>> =
        locationRepository()
            .doOnError {
                Timber.e("LocationTaskFailure: $it")
            }
}