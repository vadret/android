package fi.kroon.vadret.weatherforecast

import fi.kroon.vadret.autocomplete.model.AutoCompleteItem
import fi.kroon.vadret.common.Failure
import fi.kroon.vadret.location.LocationRepository
import fi.kroon.vadret.weatherforecast.extension.toStoredLocation
import io.github.sphrak.either.Either
import io.github.sphrak.either.flatMap
import javax.inject.Inject

/**
 *  The place is stored before the location stops being automatic, so
 *  a failure in between never leaves the forecast on the old place.
 */
internal class DefaultSelectPlaceTask @Inject constructor(
    private val locationRepository: LocationRepository
) : SelectPlaceTask {

    override suspend fun invoke(autoCompleteItem: AutoCompleteItem): Either<Failure, Unit> =
        locationRepository
            .setStoredLocation(storedLocation = autoCompleteItem.toStoredLocation())
            .flatMap { locationRepository.setAutomatic(isAutomatic = false) }
}