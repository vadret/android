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
    @AboutAppFeatureScope
    fun provideViewState(): AboutAppView.State = AboutAppView.State()

    @Provides
    @JvmStatic
    @AboutAppFeatureScope
    fun provideOnInitEventSubject(): PublishSubject<AboutAppView.Event.OnInit> =
        PublishSubject.create()

    @Provides
    @JvmStatic
    @AboutAppFeatureScope
    fun provideCompositeDisposable(): CompositeDisposable = CompositeDisposable()
}