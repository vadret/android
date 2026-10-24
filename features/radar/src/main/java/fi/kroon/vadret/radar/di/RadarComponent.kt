package fi.kroon.vadret.radar.di

import androidx.compose.runtime.Composable
import dagger.Component
import fi.kroon.vadret.core.CoreComponent
import fi.kroon.vadret.radar.RadarViewModel
import fi.kroon.vadret.ui.daggerViewModel

@RadarScope
@Component(
    dependencies = [
        CoreComponent::class
    ],
    modules = [
        RadarModule::class
    ]
)
internal interface RadarComponent {

    fun radarViewModel(): RadarViewModel

    @Component.Factory
    interface Factory {
        fun create(coreComponent: CoreComponent): RadarComponent
    }
}

@Composable
internal fun radarViewModel(): RadarViewModel =
    daggerViewModel { _, coreComponent: CoreComponent ->
        DaggerRadarComponent
            .factory()
            .create(coreComponent = coreComponent)
            .radarViewModel()
    }