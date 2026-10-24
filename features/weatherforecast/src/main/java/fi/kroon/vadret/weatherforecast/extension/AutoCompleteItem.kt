package fi.kroon.vadret.weatherforecast.extension

import fi.kroon.vadret.autocomplete.model.AutoCompleteItem
import fi.kroon.vadret.location.model.StoredLocation

internal fun AutoCompleteItem.toStoredLocation(): StoredLocation =
    StoredLocation(
        locality = locality,
        municipality = municipality,
        county = county,
        latitude = latitude,
        longitude = longitude
    )