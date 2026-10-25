package fi.kroon.vadret.favorite.local

import androidx.room3.Room
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import kotlinx.coroutines.CoroutineDispatcher

internal fun inMemoryFavoriteDatabase(coroutineDispatcher: CoroutineDispatcher): FavoriteDatabase =
    Room
        .inMemoryDatabaseBuilder<FavoriteDatabase>()
        .setDriver(driver = BundledSQLiteDriver())
        .setQueryCoroutineContext(context = coroutineDispatcher)
        .allowMainThreadQueries()
        .build()