package fi.kroon.vadret.presentation.alert.di

import dagger.Module
import dagger.Provides
import fi.kroon.vadret.presentation.alert.AlertView
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subjects.PublishSubject

@Module
object AlertModule {

    @Provides
    @JvmStatic
    @AlertScope
    fun provideOnViewInitialised(): PublishSubject<AlertView.Event.OnViewInitialised> =
        PublishSubject.create()

    @Provides
    @JvmStatic
    @AlertScope
    fun provideOnFailureHandled(): PublishSubject<AlertView.Event.OnFailureHandled> =
        PublishSubject.create()

    @Provides
    @JvmStatic
    @AlertScope
    fun provideOnShimmerEffectStarted(): PublishSubject<AlertView.Event.OnShimmerEffectStarted> =
        PublishSubject.create()

    @Provides
    @JvmStatic
    @AlertScope
    fun provideOnShimmerEffectStopped(): PublishSubject<AlertView.Event.OnShimmerEffectStopped> =
        PublishSubject.create()

    @Provides
    @JvmStatic
    @AlertScope
    fun provideOnProgressBarEffectStarted(): PublishSubject<AlertView.Event.OnProgressBarEffectStarted> =
        PublishSubject.create()

    @Provides
    @JvmStatic
    @AlertScope
    fun provideOnProgressBarEffectStopped(): PublishSubject<AlertView.Event.OnProgressBarEffectStopped> =
        PublishSubject.create()

    @Provides
    @JvmStatic
    @AlertScope
    fun provideOnSwipedToRefresh(): PublishSubject<AlertView.Event.OnSwipedToRefresh> =
        PublishSubject.create()

    @Provides
    @JvmStatic
    @AlertScope
    fun provideOnAlertListDisplayed(): PublishSubject<AlertView.Event.OnAlertListDisplayed> =
        PublishSubject.create()

    @Provides
    @JvmStatic
    @AlertScope
    fun provideOnScrollPositionRestored(): PublishSubject<AlertView.Event.OnScrollPositionRestored> =
        PublishSubject.create()

    @Provides
    @JvmStatic
    @AlertScope
    fun provideOnStateParcelUpdated(): PublishSubject<AlertView.Event.OnStateParcelUpdated> =
        PublishSubject.create()

    @Provides
    @JvmStatic
    @AlertScope
    fun provideViewState(): AlertView.State =
        AlertView.State()

    @Provides
    @JvmStatic
    @AlertScope
    fun provideCompositeDisposable(): CompositeDisposable =
        CompositeDisposable()
}