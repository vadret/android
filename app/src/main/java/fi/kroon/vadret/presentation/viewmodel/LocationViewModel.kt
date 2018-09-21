package fi.kroon.vadret.presentation.viewmodel

import fi.kroon.vadret.data.exception.Either
import fi.kroon.vadret.data.exception.Failure
import fi.kroon.vadret.data.location.exception.LocationFailure
import fi.kroon.vadret.data.location.model.Location
import fi.kroon.vadret.di.scope.VadretApplicationScope
import fi.kroon.vadret.domain.LocationUseCase
import io.reactivex.Single
import timber.log.Timber
import javax.inject.Inject

@VadretApplicationScope
class LocationViewModel @Inject constructor(
    private val locationUseCase: LocationUseCase
) : BaseViewModel() {

    fun get(): Single<Either<Failure, Location>> = locationUseCase.get()
        .doOnEvent { t1, t2 ->
            Timber.d("T1: $t1, T2: $t2")
        }.doOnError {
            Timber.d("$it")
        }.onErrorReturn {
            Either.Left(LocationFailure.LocationNotAvailableFailure())
        }
}