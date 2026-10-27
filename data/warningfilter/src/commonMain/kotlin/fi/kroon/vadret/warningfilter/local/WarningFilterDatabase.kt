package fi.kroon.vadret.warningfilter.local

import androidx.room3.ConstructedBy
import androidx.room3.Database
import androidx.room3.RoomDatabase
import androidx.room3.RoomDatabaseConstructor

private const val WARNING_FILTER_DATABASE_VERSION = 4

@Database(
    entities = [
        DistrictEntity::class,
        FeedSourcePreferenceEntity::class
    ],
    version = WARNING_FILTER_DATABASE_VERSION,
    exportSchema = false
)
@ConstructedBy(value = WarningFilterDatabaseConstructor::class)
internal abstract class WarningFilterDatabase : RoomDatabase() {
    abstract fun districtDao(): DistrictDao
    abstract fun feedSourcePreferenceDao(): FeedSourcePreferenceDao
}

@Suppress("KotlinNoActualForExpect")
internal expect object WarningFilterDatabaseConstructor : RoomDatabaseConstructor<WarningFilterDatabase> {
    override fun initialize(): WarningFilterDatabase
}