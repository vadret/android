package fi.kroon.vadret.location.extension

import fi.kroon.vadret.location.local.LocationEntity
import fi.kroon.vadret.location.model.Location

internal fun LocationEntity.toLocation(): Location =
    Location(
        latitude = latitude,
        longitude = longitude
    )