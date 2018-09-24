package fi.kroon.vadret.presentation.viewmodel

import fi.kroon.vadret.data.weather.WeatherRequest
import fi.kroon.vadret.data.exception.Either
import fi.kroon.vadret.data.exception.Failure
import fi.kroon.vadret.data.weather.model.Weather
import fi.kroon.vadret.di.scope.VadretApplicationScope
import fi.kroon.vadret.domain.WeatherUseCase
import io.reactivex.Single
import timber.log.Timber
import javax.inject.Inject

@VadretApplicationScope
class WeatherViewModel @Inject constructor(
    private val weatherUseCase: WeatherUseCase
) : BaseViewModel() {

    companion object {
        private const val FIFTEEN_SEC_IN_MILLIS = 15000
    }

    private var lastCacheInvalidationTimestamp: Long = 0
    private var forceCacheInvalidation = false

    fun get(weatherRequest: WeatherRequest): Single<Either<Failure, Weather>> {
        val res = weatherUseCase
            .get(weatherRequest, forceCacheInvalidation)
            .doOnEvent { t1, t2 ->
                Timber.d("T1: $t1, T2: $t2")
            }.doOnError {
                Timber.d("$it")
            }.onErrorReturn {
                Either.Left(Failure.IOException())
            }
        forceCacheInvalidation = false
        return res
    }

    fun forceCacheInvalidationForNextRequest() {
        if ((System.currentTimeMillis() - FIFTEEN_SEC_IN_MILLIS) <= lastCacheInvalidationTimestamp || lastCacheInvalidationTimestamp == 0L) {
            forceCacheInvalidation = true
            lastCacheInvalidationTimestamp = System.currentTimeMillis()
        }
    }
}