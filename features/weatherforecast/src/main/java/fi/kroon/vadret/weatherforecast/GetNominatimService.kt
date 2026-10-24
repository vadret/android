package fi.kroon.vadret.weatherforecast

import fi.kroon.vadret.data.failure.Failure
import fi.kroon.vadret.data.nominatim.model.Nominatim
import fi.kroon.vadret.data.nominatim.model.NominatimOut
import fi.kroon.vadret.data.nominatim.model.NominatimReverseOut
import io.github.sphrak.either.Either
import javax.inject.Inject

class GetNominatimService @Inject constructor(
    private val getNominatimTask: GetNominatimTask,
    private val getNominatimReverseTask: GetNominatimReverseTask
) {

    suspend fun get(nominatimOut: NominatimOut): Either<Failure, List<Nominatim>> =
        getNominatimTask(nominatimOut)

    suspend fun reverse(nominatimReverseOut: NominatimReverseOut): Either<Failure, Nominatim> =
        getNominatimReverseTask(nominatimReverseOut)
}