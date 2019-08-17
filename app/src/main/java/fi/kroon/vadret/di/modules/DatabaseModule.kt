package fi.kroon.vadret.di.modules

import android.app.Application
import androidx.room.Room
import dagger.Module
import dagger.Provides
import fi.kroon.vadret.data.district.local.DistrictDao
import fi.kroon.vadret.data.districtpreference.local.DistrictPreferenceDao
import fi.kroon.vadret.data.feedsource.local.FeedSourceDao
import fi.kroon.vadret.data.feedsourcepreference.local.FeedSourcePreferenceDao
import fi.kroon.vadret.data.persistance.AppDatabase
import fi.kroon.vadret.di.scope.CoreApplicationScope
import fi.kroon.vadret.util.DATABASE_NAME

@Module(
    includes = [
        ContextModule::class
    ]
)
class DatabaseModule(private val application: Application) {

    @Provides
    @CoreApplicationScope
    fun provideAppDatabase(): AppDatabase = Room.databaseBuilder(
        application,
        AppDatabase::class.java,
        DATABASE_NAME
    ).build()

    @Provides
    @CoreApplicationScope
    fun provideDistrictDao(appDatabase: AppDatabase): DistrictDao =
        appDatabase.districtDao()

    @Provides
    @CoreApplicationScope
    fun provideDistrictPreferenceDao(appDatabase: AppDatabase): DistrictPreferenceDao =
        appDatabase.districtPreferenceDao()

    @Provides
    @CoreApplicationScope
    fun provideFeedSourceDao(appDatabase: AppDatabase): FeedSourceDao =
        appDatabase.feedSourceDao()

    @Provides
    @CoreApplicationScope
    fun provideFeedSourcePreferenceDao(appDatabase: AppDatabase): FeedSourcePreferenceDao =
        appDatabase.feedSourcePreferenceDao()
}