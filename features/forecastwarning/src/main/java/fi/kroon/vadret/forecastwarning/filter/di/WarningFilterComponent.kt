package fi.kroon.vadret.forecastwarning.filter.di

import android.content.Context
import dagger.BindsInstance
import dagger.Component
import fi.kroon.vadret.core.CoreComponent
import fi.kroon.vadret.forecastwarning.filter.WarningFilterViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalCoroutinesApi
@Component(
    modules = [
        WarningFilterModule::class
    ],
    dependencies = [
        CoreComponent::class
    ]
)
@WarningFilterScope
interface WarningFilterComponent {

    fun provideWarningFilterViewModel(): WarningFilterViewModel

    @Component.Factory
    interface Factory {
        fun create(
            @BindsInstance
            context: Context,
            coreComponent: CoreComponent
        ): WarningFilterComponent
    }
}