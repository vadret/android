package fi.kroon.vadret.presentation.aboutapp.di

import dagger.Module
import dagger.Provides
import fi.kroon.vadret.presentation.aboutapp.AboutAppView
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subjects.PublishSubject

@Module
object AboutAppModule {

    @Provides
    @JvmStatic
    @AboutAppScope
    fun provideViewState(): AboutAppView.State = AboutAppView.State()

    @Provides
    @JvmStatic
    @AboutAppScope
    fun provideOnInitEventSubject(): PublishSubject<AboutAppView.Event.OnInit> =
        PublishSubject.create()

    @Provides
    @JvmStatic
    @AboutAppScope
    fun provideCompositeDisposable(): CompositeDisposable = CompositeDisposable()
}