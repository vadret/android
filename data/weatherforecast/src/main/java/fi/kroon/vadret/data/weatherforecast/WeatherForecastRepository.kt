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
import fi.kroon.vadret.util.NetworkHandler
import fi.kroon.vadret.util.extension.asLeft
import fi.kroon.vadret.util.extension.asRight
import fi.kroon.vadret.util.extension.toCoordinate
import io.github.sphrak.either.Either
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject
import kotlin.coroutines.cancellation.CancellationException

class WeatherForecastRepository @Inject constructor(
    private val weatherForecastNetDataSource: Lazy<WeatherForecastNetDataSource>,
    private val networkHandler: NetworkHandler,
    private val errorHandler: ErrorHandler,
    private val exceptionHandler: ExceptionHandler
) : IErrorHandler by errorHandler, IExceptionHandler<Failure> by exceptionHandler {

    suspend operator fun invoke(request: WeatherOut): Either<Failure, Weather> =
        withContext(Dispatchers.IO) {
            try {
                if (networkHandler.isConnected) {
                    weatherForecastNetDataSource
                        .get()
                        .getWeatherForecast(
                            request.category,
                            request.version,
                            request.longitude.toCoordinate(),
                            request.latitude.toCoordinate()
                        )?.asRight()
                        ?: WeatherForecastFailure.NoWeatherAvailable.asLeft()
                } else {
                    Failure
                        .NetworkOfflineError("error: network offline or not available")
                        .asLeft()
                }
            } catch (exception: CancellationException) {
                throw exception
            } catch (exception: Exception) {
                Timber.e(exception)
                Failure.NetworkError().asLeft()
            }
        }
}