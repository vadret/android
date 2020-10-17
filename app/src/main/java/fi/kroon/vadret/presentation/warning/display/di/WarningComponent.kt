package fi.kroon.vadret.presentation.warning.display.di

import android.content.Context
import dagger.BindsInstance
import dagger.Component
import fi.kroon.vadret.core.CoreComponent
import fi.kroon.vadret.presentation.warning.display.WarningAdapter
import fi.kroon.vadret.presentation.warning.display.WarningViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalCoroutinesApi
@Component(
    modules = [
        WarningModule::class
    ],
    dependencies = [
        CoreComponent::class
    ]
)
@WarningScope
interface WarningComponent {

    fun provideWarningViewModel(): WarningViewModel
    fun provideWarningAdapter(): WarningAdapter

    @Component.Factory
    interface Factory {
        fun create(
            @BindsInstance
            context: Context,
            coreComponent: CoreComponent
        ): WarningComponent
    }
}