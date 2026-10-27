package fi.kroon.vadret.warningfilter

import android.content.Context
import androidx.room3.Room
import fi.kroon.vadret.warningfilter.local.WarningFilterDatabase
import io.ktor.client.HttpClient

fun WarningFilterRepository(
    context: Context,
    httpClient: Lazy<HttpClient>
): WarningFilterRepository =
    WarningFilterRepository(
        databaseBuilder = Room.databaseBuilder<WarningFilterDatabase>(
            context = context,
            name = WARNING_FILTER_DATABASE_NAME
        ),
        httpClient = httpClient
    )