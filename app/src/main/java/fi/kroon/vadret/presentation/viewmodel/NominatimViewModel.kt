package fi.kroon.vadret.presentation.viewmodel

import fi.kroon.vadret.data.exception.Either
import fi.kroon.vadret.data.exception.Failure
import fi.kroon.vadret.data.nominatim.NominatimRequest
import fi.kroon.vadret.data.nominatim.NominatimRequestReverse
import fi.kroon.vadret.data.nominatim.exception.NominatimFailure
import fi.kroon.vadret.data.nominatim.model.Nominatim
import fi.kroon.vadret.di.scope.VadretApplicationScope
import fi.kroon.vadret.domain.NominatimUseCase
import io.reactivex.Single
import timber.log.Timber
import javax.inject.Inject

@VadretApplicationScope
class NominatimViewModel @Inject constructor(
    private val nominatimUseCase: NominatimUseCase
) : BaseViewModel() {

    fun get(nominatimRequest: NominatimRequest): Single<Either<Failure, List<Nominatim>>> = nominatimUseCase.get(nominatimRequest)
        .doOnEvent { t1, t2 ->
            Timber.d("T1: $t1, T2: $t2")
        }.doOnError {
            Timber.d("$it")
        }.onErrorReturn {
            Either.Left(NominatimFailure.NominatimNotAvailable())
        }

    fun reverse(nominatimRequestReverse: NominatimRequestReverse): Single<Either<Failure, Nominatim>> = nominatimUseCase.reverse(nominatimRequestReverse)
        .doOnEvent { t1, t2 ->
            Timber.d("T1: $t1, T2: $t2")
        }.doOnError {
            Timber.d("$it")
        }.onErrorReturn {
            Either.Left(NominatimFailure.NominatimNotAvailable())
        }
}