package fi.kroon.vadret.location

import android.content.SharedPreferences
import android.location.LocationManager
import fi.kroon.vadret.common.Failure
import fi.kroon.vadret.location.local.DefaultDeviceLocationLocalDataSource
import fi.kroon.vadret.location.local.DefaultStoredLocationLocalDataSource
import fi.kroon.vadret.location.model.Location
import fi.kroon.vadret.location.model.StoredLocation
import io.github.sphrak.either.Either
import kotlinx.coroutines.CoroutineDispatcher

/**
 *  Where the forecast is for: the device location while the
 *  location is automatic, otherwise the place stored last.
 */
interface LocationRepository {

    /**
     *  The last location the device knows, which needs location access.
     */
    suspend fun getDeviceLocation(): Either<Failure, Location>

    suspend fun isAutomatic(): Either<Failure, Boolean>

    suspend fun setAutomatic(isAutomatic: Boolean): Either<Failure, Unit>

    suspend fun getStoredLocation(): Either<Failure, StoredLocation>

    suspend fun setStoredLocation(storedLocation: StoredLocation): Either<Failure, Unit>
}

fun LocationRepository(
    locationManager: LocationManager,
    sharedPreferences: SharedPreferences,
    coroutineDispatcher: CoroutineDispatcher
): LocationRepository =
    DefaultLocationRepository(
        deviceLocationLocalDataSource = DefaultDeviceLocationLocalDataSource(
            locationManager = locationManager,
            coroutineDispatcher = coroutineDispatcher
        ),
        storedLocationLocalDataSource = DefaultStoredLocationLocalDataSource(
            sharedPreferences = sharedPreferences,
            coroutineDispatcher = coroutineDispatcher
        )
    )