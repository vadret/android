package fi.kroon.vadret.domain

import fi.kroon.vadret.data.exception.Either
import fi.kroon.vadret.data.exception.Failure
import fi.kroon.vadret.data.location.LocationRepository
import fi.kroon.vadret.data.location.exception.LocationFailure
import fi.kroon.vadret.data.location.model.Location
import io.reactivex.Single
import timber.log.Timber
import javax.inject.Inject

class LocationUseCase @Inject constructor(
    private val locationRepository: LocationRepository
) {
    fun get(): Single<Either<Failure, Location>> {
        return locationRepository.get()
            .doOnEvent { t1, t2 ->
                Timber.d("T1: $t1, T2: $t2")
            }.doOnError {
                Timber.d("$it")
            }.onErrorReturn { _ ->
                Either.Left(LocationFailure.LocationNotReturnedByRepository())
            }
    }
}