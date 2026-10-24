package fi.kroon.vadret.location.local

internal interface DeviceLocationLocalDataSource {

    suspend fun getLastKnownLocation(): LocationEntity
}