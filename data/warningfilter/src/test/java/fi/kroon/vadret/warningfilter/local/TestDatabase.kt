package fi.kroon.vadret.warningfilter.local

import androidx.room3.Room
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import kotlinx.coroutines.CoroutineDispatcher

/**
 *  The real schema on SQLite compiled for the JVM, so table
 *  definitions and queries run as they do on a device.
 */
internal fun inMemoryWarningFilterDatabase(coroutineDispatcher: CoroutineDispatcher): WarningFilterDatabase =
    Room
        .inMemoryDatabaseBuilder<WarningFilterDatabase>()
        .setDriver(driver = BundledSQLiteDriver())
        .setQueryCoroutineContext(context = coroutineDispatcher)
        .allowMainThreadQueries()
        .build()