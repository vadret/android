package fi.kroon.vadret.presentation.radar.di

import coil.ImageLoader
import dagger.Subcomponent
import fi.kroon.vadret.presentation.radar.RadarFragment
import fi.kroon.vadret.presentation.radar.RadarView
import fi.kroon.vadret.presentation.radar.RadarViewModel
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subjects.PublishSubject

@Subcomponent(
    modules = [
        RadarModule::class
    ]
)
@RadarScope
interface RadarComponent {

    fun inject(radarFragment: RadarFragment)

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

    @Subcomponent.Builder
    interface Builder {
        fun radarModule(module: RadarModule): Builder
        fun build(): RadarComponent
    }
}