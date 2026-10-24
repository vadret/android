package fi.kroon.vadret.data.persistance

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import fi.kroon.vadret.data.common.OffsetDateTimeTypeConverter
import fi.kroon.vadret.data.district.local.DistrictDao
import fi.kroon.vadret.data.district.model.DistrictEntity
import fi.kroon.vadret.data.district.model.DistrictOptionEntity
import fi.kroon.vadret.data.districtpreference.local.DistrictPreferenceDao
import fi.kroon.vadret.data.districtpreference.model.DistrictPreferenceEntity
import fi.kroon.vadret.data.feedsource.local.FeedSourceDao
import fi.kroon.vadret.data.feedsource.model.FeedSourceEntity
import fi.kroon.vadret.data.feedsource.model.FeedSourceOptionEntity
import fi.kroon.vadret.data.feedsourcepreference.local.FeedSourcePreferenceDao
import fi.kroon.vadret.data.feedsourcepreference.model.FeedSourcePreferenceEntity
import fi.kroon.vadret.util.DATABASE_VERSION

@Database(
    views = [
        DistrictOptionEntity::class,
        FeedSourceOptionEntity::class
    ],
    version = DATABASE_VERSION,
    entities = [
        DistrictEntity::class,
        DistrictPreferenceEntity::class,
        FeedSourceEntity::class,
        FeedSourcePreferenceEntity::class
    ],
    exportSchema = false
)
@TypeConverters(OffsetDateTimeTypeConverter::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun districtDao(): DistrictDao
    abstract fun districtPreferenceDao(): DistrictPreferenceDao

    abstract fun feedSourceDao(): FeedSourceDao
    abstract fun feedSourcePreferenceDao(): FeedSourcePreferenceDao
}