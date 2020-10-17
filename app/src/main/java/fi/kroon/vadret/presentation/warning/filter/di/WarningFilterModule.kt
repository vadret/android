package fi.kroon.vadret.presentation.warning.filter.di

import dagger.Module
import dagger.Provides
import fi.kroon.vadret.data.districtpreference.local.DistrictPreferenceDao
import fi.kroon.vadret.data.feedsourcepreference.local.FeedSourcePreferenceDao
import fi.kroon.vadret.data.persistance.AppDatabase
import fi.kroon.vadret.presentation.warning.filter.WarningFilterView
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow

@Module
@ExperimentalCoroutinesApi
object WarningFilterModule {

    @Provides
    @WarningFilterScope
    fun provideState(): WarningFilterView.State =
        WarningFilterView.State()

    @Provides
    @WarningFilterScope
    fun provideSharedFlowState(): MutableSharedFlow<WarningFilterView.State> = MutableSharedFlow()

    @Provides
    @WarningFilterScope
    fun provideDistrictPreferenceDao(appDatabase: AppDatabase): DistrictPreferenceDao =
        appDatabase.districtPreferenceDao()

    @Provides
    @WarningFilterScope
    fun provideFeedSourcePreferenceDao(appDatabase: AppDatabase): FeedSourcePreferenceDao =
        appDatabase.feedSourcePreferenceDao()
}