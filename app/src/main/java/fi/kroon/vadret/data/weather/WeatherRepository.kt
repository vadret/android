package fi.kroon.vadret.data.weather

import android.util.Log
import fi.kroon.vadret.data.Request
import fi.kroon.vadret.data.exception.Either
import fi.kroon.vadret.data.exception.Failure
import fi.kroon.vadret.data.weather.model.Weather
import fi.kroon.vadret.data.weather.net.WeatherApi
import fi.kroon.vadret.di.scope.VadretApplicationScope
import fi.kroon.vadret.utils.NetworkHandler
import io.reactivex.Single
import javax.inject.Inject

const val TAG = "WeatherRepo"

@VadretApplicationScope
class WeatherRepository @Inject constructor(
    val weatherApi: WeatherApi,
    val networkHandler: NetworkHandler
) {

    fun get(request: Request): Single<Either<Failure, Weather>> {
        return when (networkHandler.isConnected) {
            true -> Single.just(request).flatMap {
                _ -> with(request) {
                weatherApi.get(category, version, longitude, latitude).map {
                    Log.d(TAG, "Response: ${it.body()}")
                    Either.Right(it.body()!!) as Either<Failure, Weather>
                }
            }
            }.doOnEvent {
                t1, t2 -> Log.d(TAG, "T1: $t1, T2: $t2")
            }.doOnError {
                Log.d(TAG, "Error occured: $it")
            }.onErrorReturn {
                Either.Left(Failure.NetworkException())
            }
            false, null -> Single.just(Either.Left(Failure.NetworkOfflineFailure()))
        }
    }
}