package fi.kroon.vadret.domain

import android.util.Log
import fi.kroon.vadret.data.exception.Either
import fi.kroon.vadret.data.exception.Failure
import fi.kroon.vadret.data.location.model.Location
import fi.kroon.vadret.data.location.exception.LocationFailure
import fi.kroon.vadret.data.location.LocationRepository
import io.reactivex.Single
import javax.inject.Inject

class LocationUseCase @Inject constructor(
    private val locationRepository: LocationRepository
) {

    fun get(): Single<Either<Failure, Location>> {
        return locationRepository.get()
            .doOnEvent {
                t1, t2 -> Log.d(TAG, "T1: $t1, T2: $t2")
            }.doOnError {
                Log.d(TAG, "$it")
            }.onErrorReturn {
                _ -> Either.Left(LocationFailure.LocationNotReturnedByRepository())
            }
    }
}