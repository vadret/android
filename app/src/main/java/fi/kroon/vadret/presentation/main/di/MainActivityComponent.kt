package fi.kroon.vadret.presentation.main.di

import dagger.Subcomponent
import fi.kroon.vadret.presentation.main.MainActivityView
import fi.kroon.vadret.presentation.main.MainActivityViewModel
import fi.kroon.vadret.util.Scheduler
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subjects.PublishSubject

@Subcomponent(
    modules = [
        MainActivityModule::class
    ]
)
@MainActivityScope
interface MainActivityComponent {

    fun provideOnViewInitialised(): PublishSubject<MainActivityView.Event.OnViewInitialised>
    fun provideMainActivityViewModel(): MainActivityViewModel
    fun provideCompositeDisposable(): CompositeDisposable
    fun provideScheduler(): Scheduler

    @Subcomponent.Builder
    interface Builder {
        fun mainActivityModule(module: MainActivityModule): Builder
        fun build(): MainActivityComponent
    }
}