package fi.kroon.vadret.place.extension

import fi.kroon.vadret.place.local.PlaceEntity
import fi.kroon.vadret.place.model.Place

internal fun PlaceEntity.toPlace(): Place =
    Place(
        locality = locality,
        municipality = municipality,
        county = county,
        latitude = latitude,
        longitude = longitude
    )