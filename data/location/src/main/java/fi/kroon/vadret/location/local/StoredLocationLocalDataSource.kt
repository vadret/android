package fi.kroon.vadret.location.local

internal interface StoredLocationLocalDataSource {
    suspend fun isAutomatic(): Boolean
    suspend fun setAutomatic(isAutomatic: Boolean)
    suspend fun getStoredLocation(): StoredLocationEntity
    suspend fun setStoredLocation(storedLocationEntity: StoredLocationEntity)
}