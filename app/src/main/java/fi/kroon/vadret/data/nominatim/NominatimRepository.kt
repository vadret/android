package fi.kroon.vadret.data.nominatim

import fi.kroon.vadret.data.exception.Either
import fi.kroon.vadret.data.exception.Failure
import fi.kroon.vadret.data.nominatim.exception.NominatimFailure
import fi.kroon.vadret.data.nominatim.model.Nominatim
import fi.kroon.vadret.data.nominatim.net.NominatimApi
import fi.kroon.vadret.di.scope.VadretApplicationScope
import fi.kroon.vadret.utils.NetworkHandler
import io.reactivex.Single
import timber.log.Timber
import javax.inject.Inject

@VadretApplicationScope
class NominatimRepository @Inject constructor(
    private val nominatimApi: NominatimApi,
    private val networkHandler: NetworkHandler
) {
    fun get(nominatimRequest: NominatimRequest): Single<Either<Failure, List<Nominatim>>> {
        return when (networkHandler.isConnected) {
            true -> with(nominatimRequest) {
                nominatimApi.get(
                    city = city,
                    format = format,
                    limit = limit,
                    nameDetails = nameDetails,
                    countryCodes = countrycodes,
                    addressDetails = addressDetails
                ).map {
                    Timber.d("Response: ${it.body()}")
                    when (it.body()) {
                        null -> Either.Left(NominatimFailure.NominatimNotAvailable())
                        else -> Either.Right(it.body()!!)
                    } as Either<Failure, List<Nominatim>>
                }.doOnEvent { t1, t2 ->
                    Timber.d("T1: $t1, T2: $t2")
                }.doOnError {
                    Timber.d("Error occured: $it")
                }.onErrorReturn {
                    Either.Left(Failure.NetworkException())
                }
            }
            false -> Single.just(Either.Left(Failure.NetworkOfflineFailure()))
        }
    }

    fun reverse(nominatimRequestReverse: NominatimRequestReverse): Single<Either<Failure, Nominatim>> {
        return when (networkHandler.isConnected) {
            true -> with(nominatimRequestReverse) {
                nominatimApi.reverse(
                    format,
                    latitude,
                    longitude,
                    zoom
                ).map {
                    Timber.d("Response: ${it.body()}")
                    when (it.body()) {
                        null -> Either.Left(NominatimFailure.NominatimNotAvailable())
                        else -> Either.Right(it.body()!!)
                    } as Either<Failure, Nominatim>
                }.doOnEvent { t1, t2 ->
                    Timber.d("T1: $t1, T2: $t2")
                }.doOnError {
                    Timber.d("Error occured: $it")
                }.onErrorReturn {
                    Either.Left(Failure.NetworkException())
                }
            }
            false -> Single.just(Either.Left(Failure.NetworkOfflineFailure()))
        }
    }
}