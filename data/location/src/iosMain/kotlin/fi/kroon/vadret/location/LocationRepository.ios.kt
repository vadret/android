package fi.kroon.vadret.location

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import fi.kroon.vadret.location.local.DefaultDeviceLocationLocalDataSource
import kotlinx.coroutines.CoroutineDispatcher

fun LocationRepository(
    dataStore: DataStore<Preferences>,
    coroutineDispatcher: CoroutineDispatcher,
    mainDispatcher: CoroutineDispatcher
): LocationRepository =
    LocationRepository(
        deviceLocationLocalDataSource = DefaultDeviceLocationLocalDataSource(
            coroutineDispatcher = coroutineDispatcher,
            mainDispatcher = mainDispatcher
        ),
        dataStore = dataStore
    )