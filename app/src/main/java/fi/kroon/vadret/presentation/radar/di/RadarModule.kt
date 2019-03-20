package fi.kroon.vadret.presentation.radar.di

import dagger.Module
import dagger.Provides
import fi.kroon.vadret.presentation.radar.RadarView
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subjects.PublishSubject

@Module
object RadarModule {

    @Provides
    @JvmStatic
    @RadarFeatureScope
    fun provideOnViewInitialisedSubject(): PublishSubject<RadarView.Event.OnViewInitialised> =
        PublishSubject.create()

    @Provides
    @JvmStatic
    @RadarFeatureScope
    fun provideOnPlayButtonStartedSubject(): PublishSubject<RadarView.Event.OnPlayButtonStarted> =
        PublishSubject.create()

    @Provides
    @JvmStatic
    @RadarFeatureScope
    fun provideOnPlayButtonStoppedSubject(): PublishSubject<RadarView.Event.OnPlayButtonStopped> =
        PublishSubject.create()

    @Provides
    @JvmStatic
    @RadarFeatureScope
    fun provideOnFailureHandledSubject(): PublishSubject<RadarView.Event.OnFailureHandled> =
        PublishSubject.create()

    @Provides
    @JvmStatic
    @RadarFeatureScope
    fun provideOnRadarImageDisplayedSubject(): PublishSubject<RadarView.Event.OnRadarImageDisplayed> =
        PublishSubject.create()

    @Provides
    @JvmStatic
    @RadarFeatureScope
    fun provideOnSeekBarResetSubject(): PublishSubject<RadarView.Event.OnSeekBarReset> =
        PublishSubject.create()

    @Provides
    @JvmStatic
    @RadarFeatureScope
    fun provideOnSeekBarStoppedSubject(): PublishSubject<RadarView.Event.OnSeekBarStopped> =
        PublishSubject.create()

    @Provides
    @JvmStatic
    @RadarFeatureScope
    fun provideOnStateParcelUpdatedSubject(): PublishSubject<RadarView.Event.OnStateParcelUpdated> =
        PublishSubject.create()

    @Provides
    @JvmStatic
    @RadarFeatureScope
    fun provideOnPositionUpdatedSubject(): PublishSubject<RadarView.Event.OnPositionUpdated> =
        PublishSubject.create()

    @Provides
    @JvmStatic
    @RadarFeatureScope
    fun provideOnSeekBarRestoredSubject(): PublishSubject<RadarView.Event.OnSeekBarRestored> =
        PublishSubject.create()

    @Provides
    @JvmStatic
    @RadarFeatureScope
    fun provideViewState(): RadarView.State = RadarView.State()

    @Provides
    @JvmStatic
    @RadarFeatureScope
    fun provideCompositeDisposable(): CompositeDisposable = CompositeDisposable()
}