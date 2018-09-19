package fi.kroon.vadret.domain

import android.util.Log
import fi.kroon.vadret.data.Request
import fi.kroon.vadret.data.exception.Either
import fi.kroon.vadret.data.exception.Failure
import fi.kroon.vadret.data.weather.WeatherRepository
import fi.kroon.vadret.data.weather.model.Weather
import io.reactivex.Single
import javax.inject.Inject

const val TAG = "WeatherUseCase"

class WeatherUseCase @Inject constructor(
        private val weatherRepository: WeatherRepository
) {
    fun get(request: Request): Single<Either<Failure, Weather>> {
        return Single.just(request).flatMap {
            _ -> weatherRepository.get(request)
        }.doOnEvent {
            t1, t2 -> Log.d(TAG, "T1: $t1, T2: $t2")
        }.doOnError {
            Log.d(TAG,"$it")
        }
    }
}