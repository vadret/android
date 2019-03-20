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
    @AlertFeatureScope
    fun provideOnViewInitialised(): PublishSubject<AlertView.Event.OnViewInitialised> =
        PublishSubject.create()

    @Provides
    @JvmStatic
    @AlertFeatureScope
    fun provideOnFailureHandled(): PublishSubject<AlertView.Event.OnFailureHandled> =
        PublishSubject.create()

    @Provides
    @JvmStatic
    @AlertFeatureScope
    fun provideOnProgressBarEffectStarted(): PublishSubject<AlertView.Event.OnProgressBarEffectStarted> =
        PublishSubject.create()

    @Provides
    @JvmStatic
    @AlertFeatureScope
    fun provideOnProgressBarEffectStopped(): PublishSubject<AlertView.Event.OnProgressBarEffectStopped> =
        PublishSubject.create()

    @Provides
    @JvmStatic
    @AlertFeatureScope
    fun provideOnSwipedToRefresh(): PublishSubject<AlertView.Event.OnSwipedToRefresh> =
        PublishSubject.create()

    @Provides
    @JvmStatic
    @AlertFeatureScope
    fun provideOnAlertListDisplayed(): PublishSubject<AlertView.Event.OnAlertListDisplayed> =
        PublishSubject.create()

    @Provides
    @JvmStatic
    @AlertFeatureScope
    fun provideOnScrollPositionRestored(): PublishSubject<AlertView.Event.OnScrollPositionRestored> =
        PublishSubject.create()

    @Provides
    @JvmStatic
    @AlertFeatureScope
    fun provideOnStateParcelUpdated(): PublishSubject<AlertView.Event.OnStateParcelUpdated> =
        PublishSubject.create()

    @Provides
    @JvmStatic
    @AlertFeatureScope
    fun provideViewState(): AlertView.State =
        AlertView.State()

    @Provides
    @JvmStatic
    @AlertFeatureScope
    fun provideCompositeDisposable(): CompositeDisposable =
        CompositeDisposable()
}