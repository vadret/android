package fi.kroon.vadret.presentation.warning.display.di

import dagger.Module
import dagger.Provides
import fi.kroon.vadret.presentation.warning.display.WarningView
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subjects.PublishSubject

@Module
object WarningModule {

    @Provides
    @JvmStatic
    @WarningScope
    fun provideOnViewInitialised(): PublishSubject<WarningView.Event.OnViewInitialised> =
        PublishSubject.create()

    @Provides
    @JvmStatic
    @WarningScope
    fun provideOnNoWarningsIssuedDisplayed(): PublishSubject<WarningView.Event.OnNoWarningsIssuedDisplayed> =
        PublishSubject.create()

    @Provides
    @JvmStatic
    @WarningScope
    fun provideOnFailureHandled(): PublishSubject<WarningView.Event.OnFailureHandled> =
        PublishSubject.create()

    @Provides
    @JvmStatic
    @WarningScope
    fun provideOnProgressBarEffectStarted(): PublishSubject<WarningView.Event.OnProgressBarEffectStarted> =
        PublishSubject.create()

    @Provides
    @JvmStatic
    @WarningScope
    fun provideOnProgressBarEffectStopped(): PublishSubject<WarningView.Event.OnProgressBarEffectStopped> =
        PublishSubject.create()

    @Provides
    @JvmStatic
    @WarningScope
    fun provideOnSwipedToRefresh(): PublishSubject<WarningView.Event.OnSwipedToRefresh> =
        PublishSubject.create()

    @Provides
    @JvmStatic
    @WarningScope
    fun provideOnAlertListDisplayed(): PublishSubject<WarningView.Event.OnWarningListDisplayed> =
        PublishSubject.create()

    @Provides
    @JvmStatic
    @WarningScope
    fun provideOnScrollPositionRestored(): PublishSubject<WarningView.Event.OnScrollPositionRestored> =
        PublishSubject.create()

    @Provides
    @JvmStatic
    @WarningScope
    fun provideOnStateParcelUpdated(): PublishSubject<WarningView.Event.OnStateParcelUpdated> =
        PublishSubject.create()

    @Provides
    @JvmStatic
    @WarningScope
    fun provideViewState(): WarningView.State =
        WarningView.State()

    @Provides
    @JvmStatic
    @WarningScope
    fun provideCompositeDisposable(): CompositeDisposable =
        CompositeDisposable()
}