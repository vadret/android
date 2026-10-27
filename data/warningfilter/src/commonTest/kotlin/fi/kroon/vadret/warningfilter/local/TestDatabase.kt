package fi.kroon.vadret.warningfilter.local

import androidx.room3.Room
import kotlinx.coroutines.CoroutineDispatcher

internal fun inMemoryWarningFilterDatabase(coroutineDispatcher: CoroutineDispatcher): WarningFilterDatabase =
    Room
        .inMemoryDatabaseBuilder<WarningFilterDatabase>()
        .setQueryCoroutineContext(context = coroutineDispatcher)
        .forTests()
        .build()