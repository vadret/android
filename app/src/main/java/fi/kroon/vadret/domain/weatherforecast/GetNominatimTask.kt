package fi.kroon.vadret.domain.weatherforecast

import fi.kroon.vadret.data.failure.Failure
import fi.kroon.vadret.data.nominatim.NominatimRepository
import fi.kroon.vadret.data.nominatim.exception.NominatimFailure
import fi.kroon.vadret.data.nominatim.model.Nominatim
import fi.kroon.vadret.data.nominatim.model.NominatimOut
import fi.kroon.vadret.util.extension.asLeft
import io.github.sphrak.either.Either
import io.reactivex.Single
import javax.inject.Inject
import timber.log.Timber

class GetNominatimTask @Inject constructor(
    private val nominatimRepository: NominatimRepository
) {
    operator fun invoke(nominatimOut: NominatimOut): Single<Either<Failure, List<Nominatim>>> =
        nominatimRepository.get(nominatimOut)
            .doOnError {
                Timber.e("$it")
            }.onErrorReturn {
                NominatimFailure
                    .NominatimNotAvailable
                    .asLeft()
            }
}