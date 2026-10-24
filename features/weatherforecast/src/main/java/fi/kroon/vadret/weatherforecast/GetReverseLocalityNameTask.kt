package fi.kroon.vadret.weatherforecast

import fi.kroon.vadret.data.failure.Failure
import fi.kroon.vadret.data.nominatim.exception.NominatimFailure
import fi.kroon.vadret.data.nominatim.model.Nominatim
import fi.kroon.vadret.data.nominatim.model.NominatimReverseOut
import fi.kroon.vadret.util.extension.asLeft
import fi.kroon.vadret.util.extension.asRight
import io.github.sphrak.either.Either
import io.github.sphrak.either.extension.onResult
import timber.log.Timber
import javax.inject.Inject

class GetReverseLocalityNameTask @Inject constructor(
    private val task: GetNominatimReverseTask
) {

    /**
     *  Takes NominatimReverseOut as request param and returns
     *  a Nominatim object and then discern if it contains
     *  any viable name for the actionbar and returns it as
     *  a String object to the caller.
     */
    suspend operator fun invoke(nominatimReverseOut: NominatimReverseOut): Either<Failure, String?> =
        task(nominatimReverseOut).let { result: Either<Failure, Nominatim> ->
            result.onResult(
                { failure ->
                    Timber.e("Failure: $failure")
                    NominatimFailure
                        .NominatimNotAvailable
                        .asLeft()
                },
                { nominatim: Nominatim ->
                    getLocalityName(nominatim)
                        .asRight()
                }
            )
        }

    /**
     *  Discerns a viable String as Locality name
     *  town,city,village, or return "Unknown area".
     */
    private fun getLocalityName(nominatim: Nominatim): String? {
        val address = nominatim.address
        return when {
            address?.city != null -> address.city
            address?.hamlet != null -> address.hamlet
            address?.village != null -> address.village
            else -> null
        }
    }
}