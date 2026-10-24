package fi.kroon.vadret.data.radar

import dagger.Lazy
import fi.kroon.vadret.data.exception.ErrorHandler
import fi.kroon.vadret.data.exception.ExceptionHandler
import fi.kroon.vadret.data.exception.IErrorHandler
import fi.kroon.vadret.data.exception.IExceptionHandler
import fi.kroon.vadret.data.failure.Failure
import fi.kroon.vadret.data.radar.exception.RadarFailure
import fi.kroon.vadret.data.radar.model.Radar
import fi.kroon.vadret.data.radar.model.RadarRequest
import fi.kroon.vadret.data.radar.net.RadarNetDataSource
import fi.kroon.vadret.util.NetworkHandler
import fi.kroon.vadret.util.extension.asLeft
import fi.kroon.vadret.util.extension.asRight
import fi.kroon.vadret.util.extension.catchFailure
import io.github.sphrak.either.Either
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class RadarRepository @Inject constructor(
    private val radarNetDataSource: Lazy<RadarNetDataSource>,
    private val networkHandler: NetworkHandler,
    private val errorHandler: ErrorHandler,
    private val exceptionHandler: ExceptionHandler
) : IErrorHandler by errorHandler, IExceptionHandler<Failure> by exceptionHandler {

    suspend operator fun invoke(radarRequest: RadarRequest): Either<Failure, Radar> =
        withContext(Dispatchers.IO) {
            when (networkHandler.isConnected) {
                true -> catchFailure {
                    val radar: Radar? = with(radarRequest) {
                        radarNetDataSource
                            .get()
                            .getRadar(
                                year = year,
                                month = month,
                                date = date,
                                format = format,
                                timeZone = timeZone
                            )
                    }
                    when (radar?.files) {
                        null -> {
                            RadarFailure
                                .NoRadarAvailable
                                .asLeft()
                        }
                        else -> {
                            radar.asRight()
                        }
                    }
                }
                false -> getNetworkOfflineError()
            }
        }
}