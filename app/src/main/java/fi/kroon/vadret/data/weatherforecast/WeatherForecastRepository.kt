package fi.kroon.vadret.data.weatherforecast

import fi.kroon.vadret.core.di.scope.CoreApplicationScope
import fi.kroon.vadret.data.exception.Failure
import fi.kroon.vadret.data.functional.Either
import fi.kroon.vadret.data.weatherforecast.exception.WeatherForecastFailure
import fi.kroon.vadret.data.weatherforecast.model.Weather
import fi.kroon.vadret.data.weatherforecast.model.WeatherOut
import fi.kroon.vadret.data.weatherforecast.net.WeatherForecastNetDataSource
import fi.kroon.vadret.utils.HTTP_200_OK
import fi.kroon.vadret.utils.HTTP_204_NO_CONTENT
import fi.kroon.vadret.utils.HTTP_400_BAD_REQUEST
import fi.kroon.vadret.utils.HTTP_403_FORBIDDEN
import fi.kroon.vadret.utils.HTTP_404_NOT_FOUND
import fi.kroon.vadret.utils.HTTP_500_INTERNAL_SERVER_ERROR
import fi.kroon.vadret.utils.HTTP_503_SERVICE_UNAVAILABLE
import fi.kroon.vadret.utils.HTTP_504_GATEWAY_TIMEOUT
import fi.kroon.vadret.utils.NetworkHandler
import fi.kroon.vadret.utils.extensions.asLeft
import fi.kroon.vadret.utils.extensions.asSingle
import fi.kroon.vadret.utils.extensions.toCoordinate
import io.reactivex.Single
import timber.log.Timber
import javax.inject.Inject

@CoreApplicationScope
class WeatherForecastRepository @Inject constructor(
    private val weatherForecastNetDataSource: WeatherForecastNetDataSource,
    private val networkHandler: NetworkHandler
) {

    fun get(request: WeatherOut): Single<Either<Failure, Weather>> =
        when (networkHandler.isConnected) {
            true -> weatherForecastNetDataSource.get(
                request.category,
                request.version,
                request.longitude.toCoordinate(),
                request.latitude.toCoordinate()
            ).map { response ->
                when (response.code()) {
                    HTTP_200_OK -> Either.Right(response.body()!!)
                    HTTP_204_NO_CONTENT -> WeatherForecastFailure.NoWeatherAvailable.asLeft()
                    HTTP_403_FORBIDDEN -> Failure.HttpForbidden403.asLeft()
                    HTTP_404_NOT_FOUND -> WeatherForecastFailure.NoWeatherAvailableForThisLocation.asLeft()
                    HTTP_400_BAD_REQUEST -> Failure.HttpBadRequest400.asLeft()
                    HTTP_500_INTERNAL_SERVER_ERROR -> Failure.HttpInternalServerError500.asLeft()
                    HTTP_503_SERVICE_UNAVAILABLE -> Failure.HttpServiceUnavailable503.asLeft()
                    HTTP_504_GATEWAY_TIMEOUT -> Failure.HttpGatewayTimeout504.asLeft()
                    else -> WeatherForecastFailure.NoWeatherAvailable.asLeft()
                }
            }.doOnError {
                Timber.e("$it")
            }.onErrorReturn {
                Failure
                    .NetworkException
                    .asLeft()
            }
            false -> {
                Failure
                    .NetworkOfflineFailure
                    .asLeft()
                    .asSingle()
            }
        }
}