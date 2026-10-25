package fi.kroon.vadret.location

import android.location.LocationManager
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import fi.kroon.vadret.common.Failure
import fi.kroon.vadret.location.local.DefaultDeviceLocationLocalDataSource
import fi.kroon.vadret.location.local.DefaultStoredLocationLocalDataSource
import fi.kroon.vadret.location.model.Location
import fi.kroon.vadret.location.model.LocationPreference
import fi.kroon.vadret.location.model.StoredLocation
import io.github.sphrak.either.Either
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow

interface LocationRepository {
    suspend fun getDeviceLocation(): Either<Failure, Location>
    suspend fun isAutomatic(): Either<Failure, Boolean>
    suspend fun setAutomatic(isAutomatic: Boolean): Either<Failure, Unit>
    suspend fun getStoredLocation(): Either<Failure, StoredLocation>
    fun observeLocationPreference(): Flow<Either<Failure, LocationPreference>>
    suspend fun selectStoredLocation(storedLocation: StoredLocation): Either<Failure, Unit>
}

fun LocationRepository(
    locationManager: LocationManager,
    dataStore: DataStore<Preferences>,
    coroutineDispatcher: CoroutineDispatcher
): LocationRepository =
    DefaultLocationRepository(
        deviceLocationLocalDataSource = DefaultDeviceLocationLocalDataSource(
            locationManager = locationManager,
            coroutineDispatcher = coroutineDispatcher
        ),
        storedLocationLocalDataSource = DefaultStoredLocationLocalDataSource(dataStore = dataStore)
    )