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

    /**
     *  The districts, in alphabetical order, and the feed sources warnings can be
     *  shown for. The districts are fetched from SMHI the first time.
     */
    suspend fun getWarningFilter(): Either<Failure, WarningFilter>

    /**
     *  Replaces the whole selection at once.
     */
    suspend fun setWarningFilter(
        enabledDistrictIds: Set<Int>,
        enabledFeedSources: Set<FeedSource>
    ): Either<Failure, Unit>
}

/**
 *  Build one per process, it owns the database. [httpClient] is
 *  only created on first use, never on the main thread.
 */
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
                // Only the selection is lost, the districts are fetched again.
                .fallbackToDestructiveMigration(dropAllTables = true)
                .build()
        )
    )