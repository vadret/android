package fi.kroon.vadret.presentation.main.di

import dagger.Module
import dagger.Provides
import fi.kroon.vadret.presentation.main.MainActivityView
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subjects.PublishSubject

@Module
object MainActivityModule {

    @Provides
    @JvmStatic
    @MainActivityScope
    fun provideViewState(): MainActivityView.State = MainActivityView.State()

    @Provides
    @JvmStatic
    @MainActivityScope
    fun provideOnViewInitialisedSubject(): PublishSubject<MainActivityView.Event.OnViewInitialised> =
        PublishSubject.create()

    @Provides
    @JvmStatic
    @MainActivityScope
    fun provideCompositeDisposable(): CompositeDisposable = CompositeDisposable()
}