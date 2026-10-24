package fi.kroon.vadret.warningfilter.local

import androidx.room3.Database
import androidx.room3.RoomDatabase

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