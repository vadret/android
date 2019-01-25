package fi.kroon.vadret.domain

import fi.kroon.vadret.data.exception.Failure
import fi.kroon.vadret.data.functional.Either
import fi.kroon.vadret.data.nominatim.NominatimRepository
import fi.kroon.vadret.data.nominatim.exception.NominatimFailure
import fi.kroon.vadret.data.nominatim.model.Nominatim
import fi.kroon.vadret.data.nominatim.model.NominatimOut
import fi.kroon.vadret.utils.extensions.asLeft
import io.reactivex.Single
import timber.log.Timber
import javax.inject.Inject

class GetNominatimTask @Inject constructor(
    private val nominatimRepository: NominatimRepository
) {
    operator fun invoke(nominatimOut: NominatimOut): Single<Either<Failure, List<Nominatim>>> =
        nominatimRepository.get(nominatimOut)
            .doOnError {
                Timber.e("$it")
            }.onErrorReturn {
                NominatimFailure.NominatimNotAvailable().asLeft()
            }
}