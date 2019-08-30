package fi.kroon.vadret.data.nominatim

import fi.kroon.vadret.data.exception.ErrorHandler
import fi.kroon.vadret.data.exception.Failure
import fi.kroon.vadret.data.exception.IErrorHandler
import fi.kroon.vadret.data.nominatim.exception.NominatimFailure
import fi.kroon.vadret.data.nominatim.model.Nominatim
import fi.kroon.vadret.data.nominatim.model.NominatimOut
import fi.kroon.vadret.data.nominatim.model.NominatimReverseOut
import fi.kroon.vadret.data.nominatim.net.NominatimNetDataSource
import fi.kroon.vadret.di.scope.CoreApplicationScope
import fi.kroon.vadret.util.NetworkHandler
import fi.kroon.vadret.util.extension.asLeft
import fi.kroon.vadret.util.extension.asRight
import io.github.sphrak.either.Either
import io.reactivex.Single
import javax.inject.Inject
import retrofit2.Response

@CoreApplicationScope
class NominatimRepository @Inject constructor(
    private val nominatimNetDataSource: NominatimNetDataSource,
    private val networkHandler: NetworkHandler,
    private val errorHandler: ErrorHandler
) : IErrorHandler by errorHandler {
    fun get(request: NominatimOut): Single<Either<Failure, List<Nominatim>>> =
        when (networkHandler.isConnected) {
            true -> nominatimNetDataSource.get(
                city = request.city,
                format = request.format,
                limit = request.limit,
                nameDetails = request.nameDetails,
                countryCodes = request.countrycodes,
                addressDetails = request.addressDetails
            ).map { response: Response<List<Nominatim>> ->
                when (response.body()) {
                    null -> NominatimFailure
                        .NominatimNotAvailable
                        .asLeft()
                    else -> {
                        response.body()!!
                            .asRight()
                    }
                }
            }
            false -> getNetworkOfflineError()
        }

    fun reverse(request: NominatimReverseOut): Single<Either<Failure, Nominatim>> =
        when (networkHandler.isConnected) {
            true -> nominatimNetDataSource.reverse(
                format = request.format,
                latitude = request.latitude,
                longitude = request.longitude,
                zoom = request.zoom
            ).map { response ->
                when (response.body()) {
                    null -> NominatimFailure
                        .NominatimNotAvailable
                        .asLeft()
                    else -> {
                        response.body()!!
                            .asRight()
                    }
                }
            }
            false -> getNetworkOfflineError()
        }
}