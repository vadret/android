package fi.kroon.vadret.location

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import fi.kroon.vadret.common.Failure
import fi.kroon.vadret.location.local.DefaultStoredLocationLocalDataSource
import fi.kroon.vadret.location.local.DeviceLocationLocalDataSource
import fi.kroon.vadret.location.model.Location
import fi.kroon.vadret.location.model.LocationPreference
import fi.kroon.vadret.location.model.StoredLocation
import io.github.sphrak.either.Either
import kotlinx.coroutines.flow.Flow

interface LocationRepository {
    suspend fun getDeviceLocation(): Either<Failure, Location>
    suspend fun isAutomatic(): Either<Failure, Boolean>
    suspend fun setAutomatic(isAutomatic: Boolean): Either<Failure, Unit>
    suspend fun getStoredLocation(): Either<Failure, StoredLocation>
    fun observeLocationPreference(): Flow<Either<Failure, LocationPreference>>
    suspend fun selectStoredLocation(storedLocation: StoredLocation): Either<Failure, Unit>
}

internal fun LocationRepository(
    deviceLocationLocalDataSource: DeviceLocationLocalDataSource,
    dataStore: DataStore<Preferences>
): LocationRepository =
    DefaultLocationRepository(
        deviceLocationLocalDataSource = deviceLocationLocalDataSource,
        storedLocationLocalDataSource = DefaultStoredLocationLocalDataSource(dataStore = dataStore)
    )