package fi.kroon.vadret.domain

import fi.kroon.vadret.data.exception.Either
import fi.kroon.vadret.data.exception.Failure
import fi.kroon.vadret.data.nominatim.NominatimRepository
import fi.kroon.vadret.data.nominatim.NominatimRequest
import fi.kroon.vadret.data.nominatim.NominatimRequestReverse
import fi.kroon.vadret.data.nominatim.exception.NominatimFailure
import fi.kroon.vadret.data.nominatim.model.Nominatim
import io.reactivex.Single
import timber.log.Timber
import javax.inject.Inject

class NominatimUseCase @Inject constructor(
    private val nominatimRepository: NominatimRepository
) {
    fun get(nominatimRequest: NominatimRequest): Single<Either<Failure, List<Nominatim>>> {
        return nominatimRepository.get(nominatimRequest)
            .doOnEvent { t1, t2 ->
                Timber.d("T1: $t1, T2: $t2")
            }.doOnError {
                Timber.d("$it")
            }.onErrorReturn { _ ->
                Either.Left(NominatimFailure.NominatimNotAvailable())
            }
    }

    fun reverse(nominatimRequestReverse: NominatimRequestReverse): Single<Either<Failure, Nominatim>> {
        return nominatimRepository.reverse(nominatimRequestReverse)
            .doOnEvent { t1, t2 ->
                Timber.d("T1: $t1, T2: $t2")
            }.doOnError {
                Timber.d("$it")
            }.onErrorReturn { _ ->
                Either.Left(NominatimFailure.NominatimNotAvailable())
            }
    }
}