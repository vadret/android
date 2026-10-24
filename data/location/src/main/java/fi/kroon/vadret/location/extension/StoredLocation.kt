package fi.kroon.vadret.location.extension

import fi.kroon.vadret.location.local.StoredLocationEntity
import fi.kroon.vadret.location.model.StoredLocation

internal fun StoredLocation.toStoredLocationEntity(): StoredLocationEntity =
    StoredLocationEntity(
        locality = locality,
        municipality = municipality,
        county = county,
        latitude = latitude.toString(),
        longitude = longitude.toString()
    )