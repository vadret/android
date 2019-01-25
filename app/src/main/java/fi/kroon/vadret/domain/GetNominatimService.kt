package fi.kroon.vadret.domain

import fi.kroon.vadret.data.exception.Failure
import fi.kroon.vadret.data.functional.Either
import fi.kroon.vadret.data.nominatim.exception.NominatimFailure
import fi.kroon.vadret.data.nominatim.model.Nominatim
import fi.kroon.vadret.data.nominatim.model.NominatimOut
import fi.kroon.vadret.data.nominatim.model.NominatimReverseOut
import fi.kroon.vadret.utils.extensions.asLeft
import io.reactivex.Single
import timber.log.Timber
import javax.inject.Inject

class GetNominatimService @Inject constructor(
    private val getNominatimTask: GetNominatimTask,
    private val getNominatimReverseTask: GetNominatimReverseTask
) {

    fun get(nominatimOut: NominatimOut): Single<Either<Failure, List<Nominatim>>> =
        getNominatimTask(nominatimOut)
            .doOnError {
                Timber.e("$it")
            }.onErrorReturn {
                NominatimFailure.NominatimNotAvailable().asLeft()
            }

    fun reverse(nominatimReverseOut: NominatimReverseOut): Single<Either<Failure, Nominatim>> =
        getNominatimReverseTask(nominatimReverseOut)
            .doOnError {
                Timber.e("$it")
            }.onErrorReturn {
                NominatimFailure.NominatimNotAvailable().asLeft()
            }
}