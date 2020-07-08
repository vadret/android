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
import fi.kroon.vadret.di.scope.CoreApplicationScope
import fi.kroon.vadret.util.NetworkHandler
import fi.kroon.vadret.util.extension.asLeft
import fi.kroon.vadret.util.extension.asRight
import io.github.sphrak.either.Either
import io.reactivex.Single
import retrofit2.Response
import javax.inject.Inject

@CoreApplicationScope
class RadarRepository @Inject constructor(
    private val radarNetDataSource: Lazy<RadarNetDataSource>,
    private val networkHandler: NetworkHandler,
    private val errorHandler: ErrorHandler,
    private val exceptionHandler: ExceptionHandler
) : IErrorHandler by errorHandler, IExceptionHandler<Failure> by exceptionHandler {
    operator fun invoke(radarRequest: RadarRequest): Single<Either<Failure, Radar>> =
        when (networkHandler.isConnected) {
            true ->
                with(radarRequest) {
                    radarNetDataSource
                        .get()
                        .getRadar(
                            year = year,
                            month = month,
                            date = date,
                            format = format,
                            timeZone = timeZone
                        )
                }.map { response: Response<Radar> ->
                    when (response.body()?.files) {
                        null -> {
                            RadarFailure
                                .NoRadarAvailable
                                .asLeft()
                        }
                        else -> {
                            response.body()!!
                                .asRight()
                        }
                    }
                }
            false -> getNetworkOfflineError()
        }.onErrorReturn {
            exceptionHandler(it)
                .asLeft()
        }
}