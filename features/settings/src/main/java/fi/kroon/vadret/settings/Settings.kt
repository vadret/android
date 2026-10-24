package fi.kroon.vadret.settings

import fi.kroon.vadret.data.theme.model.Theme

data class Settings(
    val isGpsEnabled: Boolean,
    val theme: Theme,
    val storedLocation: StoredLocation
)