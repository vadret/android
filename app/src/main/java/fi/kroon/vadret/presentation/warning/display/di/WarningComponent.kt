package fi.kroon.vadret.presentation.warning.display.di

import dagger.Subcomponent
import fi.kroon.vadret.presentation.warning.display.WarningAdapter
import fi.kroon.vadret.presentation.warning.display.WarningFragment
import fi.kroon.vadret.presentation.warning.display.WarningView
import fi.kroon.vadret.presentation.warning.display.WarningViewModel
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subjects.PublishSubject

@Subcomponent(
    modules = [
        WarningModule::class
    ]
)
@WarningScope
interface WarningComponent {

    fun inject(warningFragment: WarningFragment)

    /**
     *  ViewModel
     */
    fun provideWarningViewModel(): WarningViewModel

    /**
     *  PublishSubject
     */
    fun provideOnViewInitialised(): PublishSubject<WarningView.Event.OnViewInitialised>
    fun provideOnProgressBarEffectStarted(): PublishSubject<WarningView.Event.OnProgressBarEffectStarted>
    fun provideOnProgressBarEffectStopped(): PublishSubject<WarningView.Event.OnProgressBarEffectStopped>
    fun provideOnSwipedToRefresh(): PublishSubject<WarningView.Event.OnSwipedToRefresh>
    fun provideOnWarningListDisplayed(): PublishSubject<WarningView.Event.OnWarningListDisplayed>
    fun provideOnScrollPositionRestored(): PublishSubject<WarningView.Event.OnScrollPositionRestored>
    fun provideOnStateParcelUpdated(): PublishSubject<WarningView.Event.OnStateParcelUpdated>
    fun provideOnFailureHandled(): PublishSubject<WarningView.Event.OnFailureHandled>
    fun provideOnNoWarningsIssuedDisplayed(): PublishSubject<WarningView.Event.OnNoWarningsIssuedDisplayed>

    /**
     *  Adapter
     */
    fun provideWarningAdapter(): WarningAdapter

    /**
     *
     */
    fun provideCompositeDisposable(): CompositeDisposable

    @Subcomponent.Builder
    interface Builder {
        fun warningModule(module: WarningModule): Builder
        fun build(): WarningComponent
    }
}