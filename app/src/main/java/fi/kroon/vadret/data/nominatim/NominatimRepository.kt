package fi.kroon.vadret.data.nominatim

import fi.kroon.vadret.data.functional.Either
import fi.kroon.vadret.data.exception.Failure
import fi.kroon.vadret.data.nominatim.exception.NominatimFailure
import fi.kroon.vadret.data.nominatim.model.Nominatim
import fi.kroon.vadret.data.nominatim.model.NominatimOut
import fi.kroon.vadret.data.nominatim.model.NominatimReverseOut
import fi.kroon.vadret.data.nominatim.net.NominatimNetDataSource
import fi.kroon.vadret.di.scope.VadretApplicationScope
import fi.kroon.vadret.utils.NetworkHandler
import fi.kroon.vadret.utils.extensions.asLeft
import io.reactivex.Single
import timber.log.Timber
import javax.inject.Inject

@VadretApplicationScope
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
            ).map { response ->
                when (response.body()) {
                    null -> NominatimFailure.NominatimNotAvailable().asLeft()
                    else -> Either.Right(response.body()!!)
                }
            }.doOnError {
                Timber.e("$it")
            }.onErrorReturn {
                Failure.NetworkException().asLeft()
            }
            false -> Single.just(Failure.NetworkOfflineFailure().asLeft())
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
                    null -> NominatimFailure.NominatimNotAvailable().asLeft()
                    else -> Either.Right(response.body()!!)
                }
            }.doOnError {
                Timber.e("$it")
            }.onErrorReturn {
                Failure.NetworkException().asLeft()
            }
            false -> Single.just(Failure.NetworkOfflineFailure().asLeft())
        }
}