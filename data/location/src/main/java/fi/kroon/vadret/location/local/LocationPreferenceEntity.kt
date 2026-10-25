package fi.kroon.vadret.location.local

internal data class LocationPreferenceEntity(
    val isAutomatic: Boolean,
    val storedLocationEntity: StoredLocationEntity
)