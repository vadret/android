package fi.kroon.vadret.data.nominatim

import dagger.Lazy
import fi.kroon.vadret.data.exception.ErrorHandler
import fi.kroon.vadret.data.exception.ExceptionHandler
import fi.kroon.vadret.data.exception.IErrorHandler
import fi.kroon.vadret.data.exception.IExceptionHandler
import fi.kroon.vadret.data.failure.Failure
import fi.kroon.vadret.data.nominatim.exception.NominatimFailure
import fi.kroon.vadret.data.nominatim.model.Nominatim
import fi.kroon.vadret.data.nominatim.model.NominatimOut
import fi.kroon.vadret.data.nominatim.model.NominatimReverseOut
import fi.kroon.vadret.data.nominatim.net.NominatimNetDataSource
import fi.kroon.vadret.util.NetworkHandler
import fi.kroon.vadret.util.extension.asLeft
import fi.kroon.vadret.util.extension.asRight
import io.github.sphrak.either.Either
import io.reactivex.Single
import retrofit2.Response
import javax.inject.Inject

class NominatimRepository @Inject constructor(
    private val nominatimNetDataSource: Lazy<NominatimNetDataSource>,
    private val networkHandler: NetworkHandler,
    private val errorHandler: ErrorHandler,
    private val exceptionHandler: ExceptionHandler
) : IErrorHandler by errorHandler, IExceptionHandler<Failure> by exceptionHandler {
    fun get(request: NominatimOut): Single<Either<Failure, List<Nominatim>>> =
        when (networkHandler.isConnected) {
            true ->
                nominatimNetDataSource.get().getNominatim(
                    city = request.city,
                    format = request.format,
                    limit = request.limit,
                    nameDetails = request.nameDetails,
                    countryCodes = request.countrycodes,
                    addressDetails = request.addressDetails
                ).map { response: Response<List<Nominatim>> ->
                    when (response.body()) {
                        null ->
                            NominatimFailure
                                .NominatimNotAvailable
                                .asLeft()
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

    fun reverse(request: NominatimReverseOut): Single<Either<Failure, Nominatim>> =
        when (networkHandler.isConnected) {
            true ->
                nominatimNetDataSource.get().getNominatimReverse(
                    format = request.format,
                    latitude = request.latitude,
                    longitude = request.longitude,
                    zoom = request.zoom
                ).map { response ->
                    when (response.body()) {
                        null ->
                            NominatimFailure
                                .NominatimNotAvailable
                                .asLeft()
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