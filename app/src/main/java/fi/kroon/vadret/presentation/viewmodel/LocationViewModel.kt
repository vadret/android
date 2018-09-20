package fi.kroon.vadret.presentation.viewmodel

import android.util.Log
import fi.kroon.vadret.data.exception.Either
import fi.kroon.vadret.data.location.exception.LocationFailure
import fi.kroon.vadret.di.scope.VadretApplicationScope
import fi.kroon.vadret.domain.LocationUseCase
import javax.inject.Inject

@VadretApplicationScope
class LocationViewModel @Inject constructor(
    private val locationUseCase: LocationUseCase
) : BaseViewModel() {

    fun get() = locationUseCase.get()
        .doOnEvent {
            t1, t2 -> Log.d(TAG, "T1: $t1, T2: $t2")
        }.doOnError {
            Log.d(TAG, "$it")
        }.onErrorReturn {
            Either.Left(LocationFailure.LocationNotAvailableFailure())
        }
}