package fi.kroon.vadret.warningfilter.local

import androidx.room3.Database
import androidx.room3.RoomDatabase

/**
 *  Version 4 replaced the per widget preference tables and their views.
 */
internal const val WARNING_FILTER_DATABASE_VERSION = 4

@Database(
    entities = [
        DistrictEntity::class,
        FeedSourcePreferenceEntity::class
    ],
    version = WARNING_FILTER_DATABASE_VERSION,
    exportSchema = false
)
internal abstract class WarningFilterDatabase : RoomDatabase() {
    abstract fun districtDao(): DistrictDao
    abstract fun feedSourcePreferenceDao(): FeedSourcePreferenceDao
}