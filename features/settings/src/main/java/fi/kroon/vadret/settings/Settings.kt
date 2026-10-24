package fi.kroon.vadret.settings

import fi.kroon.vadret.location.model.StoredLocation
import fi.kroon.vadret.theme.model.Theme

data class Settings(
    val isAutomaticLocation: Boolean,
    val theme: Theme,
    val storedLocation: StoredLocation
)