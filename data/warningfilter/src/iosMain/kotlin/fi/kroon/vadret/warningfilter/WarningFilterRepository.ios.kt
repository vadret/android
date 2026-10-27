package fi.kroon.vadret.warningfilter

import androidx.room3.Room
import androidx.sqlite.driver.NativeSQLiteDriver
import fi.kroon.vadret.warningfilter.local.WarningFilterDatabase
import io.ktor.client.HttpClient
import kotlinx.coroutines.CoroutineDispatcher

fun WarningFilterRepository(
    databaseDirectory: String,
    httpClient: Lazy<HttpClient>,
    coroutineDispatcher: CoroutineDispatcher
): WarningFilterRepository =
    WarningFilterRepository(
        databaseBuilder = Room
            .databaseBuilder<WarningFilterDatabase>(name = "$databaseDirectory/$WARNING_FILTER_DATABASE_NAME")
            .setDriver(driver = NativeSQLiteDriver())
            .setQueryCoroutineContext(context = coroutineDispatcher),
        httpClient = httpClient
    )