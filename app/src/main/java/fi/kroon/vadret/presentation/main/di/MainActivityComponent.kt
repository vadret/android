package fi.kroon.vadret.presentation.main.di

import android.content.Context
import dagger.BindsInstance
import dagger.Component
import fi.kroon.vadret.core.CoreComponent
import fi.kroon.vadret.presentation.main.MainActivityView
import fi.kroon.vadret.presentation.main.MainActivityViewModel
import fi.kroon.vadret.util.Scheduler
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subjects.PublishSubject

@Component(
    modules = [
        MainActivityModule::class
    ],
    dependencies = [
        CoreComponent::class
    ]
)
@MainActivityScope
interface MainActivityComponent {

    fun provideOnViewInitialised(): PublishSubject<MainActivityView.Event.OnViewInitialised>
    fun provideMainActivityViewModel(): MainActivityViewModel
    fun provideCompositeDisposable(): CompositeDisposable
    fun provideScheduler(): Scheduler

    @Component.Factory
    interface Factory {
        fun create(
            @BindsInstance
            context: Context,
            coreComponent: CoreComponent
        ): MainActivityComponent
    }
}