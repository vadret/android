package fi.kroon.vadret.presentation.viewmodel

import fi.kroon.vadret.data.Request
import fi.kroon.vadret.data.exception.Either
import fi.kroon.vadret.data.exception.Failure
import fi.kroon.vadret.data.weather.model.Weather
import fi.kroon.vadret.di.scope.VadretApplicationScope
import fi.kroon.vadret.domain.WeatherUseCase
import io.reactivex.Single
import timber.log.Timber
import javax.inject.Inject

const val TAG = "WeatherViewModel"

@VadretApplicationScope
class WeatherViewModel @Inject constructor(
    private val weatherUseCase: WeatherUseCase
) : BaseViewModel() {

    fun get(request: Request): Single<Either<Failure, Weather>> = weatherUseCase
        .get(request)
        .doOnEvent { t1, t2 ->
            Timber.d("T1: $t1, T2: $t2")
        }.doOnError {
            Timber.d("$it")
        }.onErrorReturn {
            Either.Left(Failure.IOException())
        }
}