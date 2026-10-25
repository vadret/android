package fi.kroon.vadret.location.model

data class LocationPreference(
    val isAutomatic: Boolean,
    val storedLocation: StoredLocation
)