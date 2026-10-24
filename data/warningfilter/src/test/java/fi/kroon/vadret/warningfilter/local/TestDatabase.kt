package fi.kroon.vadret.warningfilter.local

import androidx.room3.Room
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import kotlinx.coroutines.CoroutineDispatcher

internal fun inMemoryWarningFilterDatabase(coroutineDispatcher: CoroutineDispatcher): WarningFilterDatabase =
    Room
        .inMemoryDatabaseBuilder<WarningFilterDatabase>()
        .setDriver(driver = BundledSQLiteDriver())
        .setQueryCoroutineContext(context = coroutineDispatcher)
        .allowMainThreadQueries()
        .build()