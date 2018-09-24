package fi.kroon.vadret.data.weather

import fi.kroon.vadret.data.HEADER_NO_CACHE
import fi.kroon.vadret.data.exception.Either
import fi.kroon.vadret.data.exception.Failure
import fi.kroon.vadret.data.weather.model.Weather
import fi.kroon.vadret.data.weather.net.WeatherApi
import fi.kroon.vadret.di.scope.VadretApplicationScope
import fi.kroon.vadret.utils.NetworkHandler
import io.reactivex.Single
import timber.log.Timber
import javax.inject.Inject

@VadretApplicationScope
class WeatherRepository @Inject constructor(
    private val weatherApi: WeatherApi,
    val networkHandler: NetworkHandler
) {
    fun get(weatherRequest: WeatherRequest, forceCacheInvalidation: Boolean = false): Single<Either<Failure, Weather>> =
        Single.just(weatherRequest).flatMap { _ ->
            with(weatherRequest) {
                weatherApi.get(category, version, longitude, latitude, getCacheHeader(forceCacheInvalidation)).map {
                    Timber.d("Response: ${it.body()}")
                    Either.Right(it.body()!!) as Either<Failure, Weather>
                }
            }
        }.doOnEvent { t1, t2 ->
            Timber.d("T1: $t1, T2: $t2")
        }.doOnError {
            Timber.d("Error occurred: $it")
        }.onErrorReturn {
            Either.Left(Failure.NetworkException())
        }

    private fun getCacheHeader(forceCacheInvalidation: Boolean) = if (networkHandler.isConnected && forceCacheInvalidation) {
        // Allow invalidation only if there is a chance of getting new data
        HEADER_NO_CACHE
    } else {
        null
    }
}