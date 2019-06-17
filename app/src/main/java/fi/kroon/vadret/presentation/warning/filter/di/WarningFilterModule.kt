package fi.kroon.vadret.presentation.warning.filter.di

import dagger.Module
import dagger.Provides
import fi.kroon.vadret.data.district.model.DistrictOptionEntity
import fi.kroon.vadret.data.feedsource.model.FeedSourceOptionEntity
import fi.kroon.vadret.presentation.warning.filter.WarningFilterView
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subjects.PublishSubject

@Module
object WarningFilterModule {

    @Provides
    @JvmStatic
    @WarningFilterScope
    fun provideDistrictOptionModel(): PublishSubject<DistrictOptionEntity> =
        PublishSubject.create()

    @Provides
    @JvmStatic
    @WarningFilterScope
    fun provideFeedSourceOptionModel(): PublishSubject<FeedSourceOptionEntity> =
        PublishSubject.create()

    @Provides
    @JvmStatic
    @WarningFilterScope
    fun provideOnFilterOptionsDisplayed(): PublishSubject<WarningFilterView.Event.OnFilterOptionsDisplayed> =
        PublishSubject.create()

    @Provides
    @JvmStatic
    @WarningFilterScope
    fun provideOnViewInitialised(): PublishSubject<WarningFilterView.Event.OnViewInitialised> =
        PublishSubject.create()

    @Provides
    @JvmStatic
    @WarningFilterScope
    fun provideViewState(): WarningFilterView.State =
        WarningFilterView.State()

    @Provides
    @JvmStatic
    @WarningFilterScope
    fun provideCompositeDisposable(): CompositeDisposable =
        CompositeDisposable()
}