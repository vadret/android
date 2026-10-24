package fi.kroon.vadret.weatherforecast

import fi.kroon.vadret.data.failure.Failure
import fi.kroon.vadret.data.nominatim.NominatimRepository
import fi.kroon.vadret.data.nominatim.model.Nominatim
import fi.kroon.vadret.data.nominatim.model.NominatimOut
import io.github.sphrak.either.Either
import javax.inject.Inject

class GetNominatimTask @Inject constructor(
    private val nominatimRepository: NominatimRepository
) {
    suspend operator fun invoke(nominatimOut: NominatimOut): Either<Failure, List<Nominatim>> =
        nominatimRepository.get(nominatimOut)
}