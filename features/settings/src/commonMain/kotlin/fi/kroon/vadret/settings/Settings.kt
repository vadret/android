package fi.kroon.vadret.settings

import fi.kroon.vadret.language.model.AppLanguage
import fi.kroon.vadret.location.model.StoredLocation
import fi.kroon.vadret.theme.model.Theme

internal data class Settings(
    val isAutomaticLocation: Boolean,
    val theme: Theme,
    val language: AppLanguage,
    val storedLocation: StoredLocation
)