package fi.kroon.vadret.main.di

import dagger.Module
import dagger.Provides
import fi.kroon.vadret.core.R
import fi.kroon.vadret.data.theme.model.ThemeStyles
import fi.kroon.vadret.main.MainActivityView
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
    fun provideThemeStyles(): ThemeStyles = ThemeStyles(
        light = R.style.LIGHT,
        dark = R.style.DARK,
        amoled = R.style.AMOLED
    )

    @Provides
    @MainActivityScope
    fun provideSchedulers(): Scheduler = Scheduler()
}