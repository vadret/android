package fi.kroon.vadret.presentation.main.di

import dagger.Module
import dagger.Provides
import fi.kroon.vadret.presentation.main.MainActivityView
import fi.kroon.vadret.util.Scheduler
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subjects.PublishSubject

@Module
object MainActivityModule {

    @Provides
    @MainActivityScope
    fun provideViewState(): MainActivityView.State = MainActivityView.State()

    @Provides
    @MainActivityScope
    fun provideOnViewInitialisedSubject(): PublishSubject<MainActivityView.Event.OnViewInitialised> =
        PublishSubject.create()

    @Provides
    @MainActivityScope
    fun provideCompositeDisposable(): CompositeDisposable = CompositeDisposable()

    @Provides
    @MainActivityScope
    fun provideSchedulers(): Scheduler = Scheduler()
}