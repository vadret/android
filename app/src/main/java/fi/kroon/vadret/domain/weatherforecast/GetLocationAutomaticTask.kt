package fi.kroon.vadret.domain.weatherforecast

import fi.kroon.vadret.data.functional.Either
import fi.kroon.vadret.data.exception.Failure
import fi.kroon.vadret.data.location.LocationRepository
import fi.kroon.vadret.data.location.model.Location
import io.reactivex.Single
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