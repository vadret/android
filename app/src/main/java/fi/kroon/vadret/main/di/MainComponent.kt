package fi.kroon.vadret.main.di

import androidx.compose.runtime.Composable
import dagger.Component
import fi.kroon.vadret.core.CoreComponent
import fi.kroon.vadret.main.MainViewModel
import fi.kroon.vadret.ui.daggerViewModel

@MainScope
@Component(
    dependencies = [
        CoreComponent::class
    ]
)
internal interface MainComponent {

    fun mainViewModel(): MainViewModel

    @Component.Factory
    interface Factory {
        fun create(coreComponent: CoreComponent): MainComponent
    }
}

@Composable
internal fun mainViewModel(): MainViewModel =
    daggerViewModel { _, coreComponent: CoreComponent ->
        DaggerMainComponent
            .factory()
            .create(coreComponent = coreComponent)
            .mainViewModel()
    }