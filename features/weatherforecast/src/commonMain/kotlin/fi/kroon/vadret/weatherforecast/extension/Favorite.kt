package fi.kroon.vadret.weatherforecast.extension

import fi.kroon.vadret.favorite.model.Favorite
import fi.kroon.vadret.location.model.StoredLocation

internal fun Favorite.toStoredLocation(): StoredLocation =
    StoredLocation(
        locality = locality,
        municipality = municipality,
        county = county,
        latitude = latitude,
        longitude = longitude
    )