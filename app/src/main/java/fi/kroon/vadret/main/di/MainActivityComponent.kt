package fi.kroon.vadret.main.di

import android.content.Context
import dagger.BindsInstance
import dagger.Component
import fi.kroon.vadret.core.CoreComponent
import fi.kroon.vadret.main.MainActivityViewModel

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

    fun provideMainActivityViewModel(): MainActivityViewModel

    @Component.Factory
    interface Factory {
        fun create(
            @BindsInstance
            context: Context,
            coreComponent: CoreComponent
        ): MainActivityComponent
    }
}