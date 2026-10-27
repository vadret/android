package fi.kroon.vadret.location.extension

import fi.kroon.vadret.location.local.StoredLocationEntity
import fi.kroon.vadret.location.model.StoredLocation

internal fun StoredLocationEntity.toStoredLocation(): StoredLocation =
    StoredLocation(
        locality = locality,
        municipality = municipality,
        county = county,
        latitude = latitude,
        longitude = longitude
    )