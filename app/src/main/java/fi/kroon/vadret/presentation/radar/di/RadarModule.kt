package fi.kroon.vadret.presentation.radar.di

import android.content.Context
import coil.ImageLoader
import dagger.Module
import dagger.Provides
import fi.kroon.vadret.presentation.radar.RadarView
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subjects.PublishSubject

@Module
object RadarModule {

    @Provides
    @RadarScope
    fun provideOnViewInitialisedSubject(): PublishSubject<RadarView.Event.OnViewInitialised> =
        PublishSubject.create()

    @Provides
    @RadarScope
    fun provideOnPlayButtonStartedSubject(): PublishSubject<RadarView.Event.OnPlayButtonStarted> =
        PublishSubject.create()

    @Provides
    @RadarScope
    fun provideOnPlayButtonStoppedSubject(): PublishSubject<RadarView.Event.OnPlayButtonStopped> =
        PublishSubject.create()

    @Provides
    @RadarScope
    fun provideOnFailureHandledSubject(): PublishSubject<RadarView.Event.OnFailureHandled> =
        PublishSubject.create()

    @Provides
    @RadarScope
    fun provideOnRadarImageDisplayedSubject(): PublishSubject<RadarView.Event.OnRadarImageDisplayed> =
        PublishSubject.create()

    @Provides
    @RadarScope
    fun provideOnSeekBarResetSubject(): PublishSubject<RadarView.Event.OnSeekBarReset> =
        PublishSubject.create()

    @Provides
    @RadarScope
    fun provideOnSeekBarStoppedSubject(): PublishSubject<RadarView.Event.OnSeekBarStopped> =
        PublishSubject.create()

    @Provides
    @RadarScope
    fun provideOnStateParcelUpdatedSubject(): PublishSubject<RadarView.Event.OnStateParcelUpdated> =
        PublishSubject.create()

    @Provides
    @RadarScope
    fun provideOnPositionUpdatedSubject(): PublishSubject<RadarView.Event.OnPositionUpdated> =
        PublishSubject.create()

    @Provides
    @RadarScope
    fun provideOnSeekBarRestoredSubject(): PublishSubject<RadarView.Event.OnSeekBarRestored> =
        PublishSubject.create()

    @Provides
    @RadarScope
    fun provideViewState(): RadarView.State = RadarView.State()

    @Provides
    @RadarScope
    fun provideCompositeDisposable(): CompositeDisposable = CompositeDisposable()

    @Provides
    @RadarScope
    fun provideImageLoader(context: Context): ImageLoader =
        ImageLoader
            .Builder(context)
            .availableMemoryPercentage(0.25)
            .crossfade(true)
            .build()
}