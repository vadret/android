package fi.kroon.vadret.warningfilter

import android.content.Context
import androidx.room3.Room
import fi.kroon.vadret.aggregatedfeed.model.FeedSource
import fi.kroon.vadret.common.Failure
import fi.kroon.vadret.warningfilter.local.DefaultWarningFilterLocalDataSource
import fi.kroon.vadret.warningfilter.local.WarningFilterDatabase
import fi.kroon.vadret.warningfilter.model.WarningFilter
import fi.kroon.vadret.warningfilter.remote.DefaultDistrictRemoteDataSource
import io.github.sphrak.either.Either
import io.ktor.client.HttpClient

private const val DATABASE_NAME = "vadret.db"

interface WarningFilterRepository {

    suspend fun getWarningFilter(): Either<Failure, WarningFilter>

    suspend fun setWarningFilter(
        enabledDistrictIds: Set<Int>,
        enabledFeedSources: Set<FeedSource>
    ): Either<Failure, Unit>
}

fun WarningFilterRepository(
    context: Context,
    httpClient: Lazy<HttpClient>
): WarningFilterRepository =
    DefaultWarningFilterRepository(
        districtRemoteDataSource = DefaultDistrictRemoteDataSource(httpClient = httpClient),
        warningFilterLocalDataSource = DefaultWarningFilterLocalDataSource(
            warningFilterDatabase = Room
                .databaseBuilder<WarningFilterDatabase>(
                    context = context,
                    name = DATABASE_NAME
                )
                .fallbackToDestructiveMigration(dropAllTables = true)
                .build()
        )
    )