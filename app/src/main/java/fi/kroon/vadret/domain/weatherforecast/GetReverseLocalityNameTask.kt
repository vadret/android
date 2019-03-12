package fi.kroon.vadret.domain.weatherforecast

import fi.kroon.vadret.data.exception.Failure
import fi.kroon.vadret.data.functional.Either
import fi.kroon.vadret.data.nominatim.exception.NominatimFailure
import fi.kroon.vadret.data.nominatim.model.Nominatim
import fi.kroon.vadret.data.nominatim.model.NominatimReverseOut
import fi.kroon.vadret.utils.extensions.asLeft
import fi.kroon.vadret.utils.extensions.asRight
import io.reactivex.Single
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
    operator fun invoke(nominatimReverseOut: NominatimReverseOut): Single<Either<Failure, String?>> =
        task(nominatimReverseOut).map { result: Either<Failure, Nominatim> ->
            result.either(
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
        return with(nominatim) {
            when {
                address?.city != null -> address.city
                address?.hamlet != null -> address.hamlet
                address?.village != null -> address.village
                else -> null
            }
        }
    }
}