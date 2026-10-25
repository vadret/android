package fi.kroon.vadret.location.extension

import fi.kroon.vadret.location.local.LocationPreferenceEntity
import fi.kroon.vadret.location.model.LocationPreference

internal fun LocationPreferenceEntity.toLocationPreference(): LocationPreference =
    LocationPreference(
        isAutomatic = isAutomatic,
        storedLocation = storedLocationEntity.toStoredLocation()
    )