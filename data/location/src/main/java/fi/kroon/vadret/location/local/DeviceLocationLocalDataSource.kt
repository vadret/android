package fi.kroon.vadret.location.local

internal interface DeviceLocationLocalDataSource {

    /**
     *  Throws when no location provider is enabled or none
     *  of them knows a location yet.
     */
    suspend fun getLastKnownLocation(): LocationEntity
}