package fi.kroon.vadret.weatherforecast

import fi.kroon.vadret.autocomplete.model.AutoCompleteItem
import fi.kroon.vadret.common.Failure
import fi.kroon.vadret.location.LocationRepository
import fi.kroon.vadret.weatherforecast.extension.toStoredLocation
import io.github.sphrak.either.Either
import io.github.sphrak.either.flatMap

internal class DefaultSelectPlaceTask(
    private val locationRepository: LocationRepository
) : SelectPlaceTask {

    override suspend fun invoke(autoCompleteItem: AutoCompleteItem): Either<Failure, Unit> =
        locationRepository
            .setStoredLocation(storedLocation = autoCompleteItem.toStoredLocation())
            .flatMap { locationRepository.setAutomatic(isAutomatic = false) }
}