package fi.kroon.vadret.presentation.radar.di

import android.content.Context
import coil.ImageLoader
import dagger.BindsInstance
import dagger.Component
import fi.kroon.vadret.core.CoreComponent
import fi.kroon.vadret.presentation.radar.RadarView
import fi.kroon.vadret.presentation.radar.RadarViewModel
import fi.kroon.vadret.util.Scheduler
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subjects.PublishSubject

@Component(
    modules = [
        RadarModule::class
    ],
    dependencies = [
        CoreComponent::class
    ]
)
@RadarScope
interface RadarComponent {

    /**
     *  ViewModel
     */
    fun provideRadarViewModel(): RadarViewModel

    /**
     *  PublishSubject
     */
    fun provideOnViewInitialised(): PublishSubject<RadarView.Event.OnViewInitialised>
    fun provideOnFailureHandled(): PublishSubject<RadarView.Event.OnFailureHandled>
    fun provideOnRadarImageDisplayed(): PublishSubject<RadarView.Event.OnRadarImageDisplayed>
    fun provideOnSeekBarStopped(): PublishSubject<RadarView.Event.OnSeekBarStopped>
    fun provideOnStateParcelUpdated(): PublishSubject<RadarView.Event.OnStateParcelUpdated>
    fun provideOnPlayButtonStarted(): PublishSubject<RadarView.Event.OnPlayButtonStarted>
    fun provideOnPlayButtonStopped(): PublishSubject<RadarView.Event.OnPlayButtonStopped>
    fun provideOnSeekBarReset(): PublishSubject<RadarView.Event.OnSeekBarReset>
    fun provideOnPositionUpdated(): PublishSubject<RadarView.Event.OnPositionUpdated>
    fun provideOnSeekBarRestored(): PublishSubject<RadarView.Event.OnSeekBarRestored>
    fun provideCompositeDisposable(): CompositeDisposable
    fun provideImageLoader(): ImageLoader
    fun provideScheduler(): Scheduler

    @Component.Factory
    interface Factory {
        fun create(
            @BindsInstance
            context: Context,
            coreComponent: CoreComponent
        ): RadarComponent
    }
}