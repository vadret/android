package fi.kroon.vadret.data.weatherforecast

import dagger.Lazy
import fi.kroon.vadret.data.exception.ErrorHandler
import fi.kroon.vadret.data.exception.ExceptionHandler
import fi.kroon.vadret.data.exception.IErrorHandler
import fi.kroon.vadret.data.exception.IExceptionHandler
import fi.kroon.vadret.data.failure.Failure
import fi.kroon.vadret.data.weatherforecast.exception.WeatherForecastFailure
import fi.kroon.vadret.data.weatherforecast.model.Weather
import fi.kroon.vadret.data.weatherforecast.model.WeatherOut
import fi.kroon.vadret.data.weatherforecast.net.WeatherForecastNetDataSource
import fi.kroon.vadret.util.HTTP_200_OK
import fi.kroon.vadret.util.NetworkHandler
import fi.kroon.vadret.util.extension.asLeft
import fi.kroon.vadret.util.extension.asRight
import fi.kroon.vadret.util.extension.toCoordinate
import io.github.sphrak.either.Either
import retrofit2.Response
import timber.log.Timber
import javax.inject.Inject

class WeatherForecastRepository @Inject constructor(
    private val weatherForecastNetDataSource: Lazy<WeatherForecastNetDataSource>,
    private val networkHandler: NetworkHandler,
    private val errorHandler: ErrorHandler,
    private val exceptionHandler: ExceptionHandler
) : IErrorHandler by errorHandler, IExceptionHandler<Failure> by exceptionHandler {

    suspend operator fun invoke(request: WeatherOut): Either<Failure, Weather> =
        try {
            if (networkHandler.isConnected) {
                weatherForecastNetDataSource
                    .get()
                    .getWeatherForecast(
                        request.category,
                        request.version,
                        request.longitude.toCoordinate(),
                        request.latitude.toCoordinate()
                    ).let { response: Response<Weather> ->
                        if (response.code() == HTTP_200_OK) {
                            response.body()?.asRight() ?: WeatherForecastFailure.NoWeatherAvailable.asLeft()
                        } else {
                            WeatherForecastFailure.NoWeatherAvailable.asLeft()
                        }
                    }
            } else {
                Failure
                    .NetworkOfflineError("error: network offline or not available")
                    .asLeft()
            }
        } catch (exception: Exception) {
            Timber.e(exception)
            Failure.NetworkError().asLeft()
        }
}