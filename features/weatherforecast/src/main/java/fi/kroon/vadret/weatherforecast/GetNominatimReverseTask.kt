package fi.kroon.vadret.weatherforecast

import fi.kroon.vadret.data.failure.Failure
import fi.kroon.vadret.data.nominatim.NominatimRepository
import fi.kroon.vadret.data.nominatim.model.Nominatim
import fi.kroon.vadret.data.nominatim.model.NominatimReverseOut
import io.github.sphrak.either.Either
import javax.inject.Inject

class GetNominatimReverseTask @Inject constructor(
    private val nominatimRepository: NominatimRepository
) {
    suspend operator fun invoke(nominatimReverseOut: NominatimReverseOut): Either<Failure, Nominatim> =
        nominatimRepository.reverse(nominatimReverseOut)
}