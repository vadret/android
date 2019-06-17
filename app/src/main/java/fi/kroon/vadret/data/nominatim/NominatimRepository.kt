package fi.kroon.vadret.data.nominatim

import fi.kroon.vadret.di.scope.CoreApplicationScope
import fi.kroon.vadret.data.exception.Failure
import fi.kroon.vadret.data.functional.Either
import fi.kroon.vadret.data.nominatim.exception.NominatimFailure
import fi.kroon.vadret.data.nominatim.model.Nominatim
import fi.kroon.vadret.data.nominatim.model.NominatimOut
import fi.kroon.vadret.data.nominatim.model.NominatimReverseOut
import fi.kroon.vadret.data.nominatim.net.NominatimNetDataSource
import fi.kroon.vadret.util.NetworkHandler
import fi.kroon.vadret.util.extension.asLeft
import fi.kroon.vadret.util.extension.asRight
import fi.kroon.vadret.util.extension.asSingle
import io.reactivex.Single
import retrofit2.Response
import timber.log.Timber
import javax.inject.Inject

@CoreApplicationScope
class NominatimRepository @Inject constructor(
    private val nominatimNetDataSource: NominatimNetDataSource,
    private val networkHandler: NetworkHandler
) {
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