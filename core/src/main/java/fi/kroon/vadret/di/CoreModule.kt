package fi.kroon.vadret.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import fi.kroon.vadret.common.DiskCache
import fi.kroon.vadret.idgenerator.IdGenerator
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import okio.Path.Companion.toPath
import org.koin.core.module.Module
import org.koin.dsl.module
import java.io.File
import kotlin.time.Clock

private const val SETTINGS_FILE_NAME = "settings.preferences_pb"
private const val DISK_CACHE_DIRECTORY = "disk_cache"

val coreModule: Module = module {
    factory<Clock> { Clock.System }
    factory<CoroutineDispatcher> { Dispatchers.IO }
    single<DataStore<Preferences>> {
        val settingsFile = File(get<Context>().filesDir, SETTINGS_FILE_NAME)
        PreferenceDataStoreFactory.createWithPath(produceFile = { settingsFile.absolutePath.toPath() })
    }
    single<IdGenerator> { IdGenerator() }
    single<DiskCache> {
        DiskCache(directory = File(get<Context>().cacheDir, DISK_CACHE_DIRECTORY))
    }
}