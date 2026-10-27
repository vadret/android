package fi.kroon.vadret.location

import android.location.LocationManager
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import fi.kroon.vadret.location.local.DefaultDeviceLocationLocalDataSource
import kotlinx.coroutines.CoroutineDispatcher

fun LocationRepository(
    locationManager: LocationManager,
    dataStore: DataStore<Preferences>,
    coroutineDispatcher: CoroutineDispatcher
): LocationRepository =
    LocationRepository(
        deviceLocationLocalDataSource = DefaultDeviceLocationLocalDataSource(
            locationManager = locationManager,
            coroutineDispatcher = coroutineDispatcher
        ),
        dataStore = dataStore
    )