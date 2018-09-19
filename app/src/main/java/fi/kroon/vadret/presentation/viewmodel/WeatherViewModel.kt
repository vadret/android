package fi.kroon.vadret.presentation.viewmodel

import android.util.Log
import fi.kroon.vadret.data.Request
import fi.kroon.vadret.data.exception.Either
import fi.kroon.vadret.data.exception.Failure
import fi.kroon.vadret.di.scope.VadretApplicationScope
import fi.kroon.vadret.domain.WeatherUseCase
import javax.inject.Inject

const val TAG = "WeatherViewModel"

@VadretApplicationScope
class WeatherViewModel @Inject constructor(
        private val weatherUseCase: WeatherUseCase
): BaseViewModel() {

    fun get(request: Request) = weatherUseCase
            .get(request)
            .doOnEvent {
                t1, t2 -> Log.d(TAG, "T1: $t1, T2: $t2")
            }.doOnError {
                Log.d(TAG, "$it")
            }.onErrorReturn {
                Either.Left(Failure.IOException())
            }
}