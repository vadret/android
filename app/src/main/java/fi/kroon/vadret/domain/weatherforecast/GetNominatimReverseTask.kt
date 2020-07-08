package fi.kroon.vadret.domain.weatherforecast

import fi.kroon.vadret.data.failure.Failure
import fi.kroon.vadret.data.nominatim.NominatimRepository
import fi.kroon.vadret.data.nominatim.exception.NominatimFailure
import fi.kroon.vadret.data.nominatim.model.Nominatim
import fi.kroon.vadret.data.nominatim.model.NominatimReverseOut
import fi.kroon.vadret.util.extension.asLeft
import io.github.sphrak.either.Either
import io.reactivex.Single
import timber.log.Timber
import javax.inject.Inject

class GetNominatimReverseTask @Inject constructor(
    private val nominatimRepository: NominatimRepository
) {
    operator fun invoke(nominatimReverseOut: NominatimReverseOut): Single<Either<Failure, Nominatim>> =
        nominatimRepository.reverse(nominatimReverseOut)
            .doOnError {
                Timber.e("$it")
            }.onErrorReturn {
                NominatimFailure
                    .NominatimNotAvailable
                    .asLeft()
            }
}