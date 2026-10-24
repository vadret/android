package fi.kroon.vadret.radar.di

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import dagger.Component
import fi.kroon.vadret.radar.RadarViewModel

@RadarScope
@Component(
    dependencies = [
        RadarDependencies::class
    ],
    modules = [
        RadarModule::class
    ]
)
internal interface RadarComponent {

    fun radarViewModel(): RadarViewModel

    @Component.Factory
    interface Factory {
        fun create(radarDependencies: RadarDependencies): RadarComponent
    }
}

/**
 *  One view model per navigation entry.
 */
@Composable
internal fun radarViewModel(radarDependencies: RadarDependencies): RadarViewModel =
    viewModel {
        DaggerRadarComponent
            .factory()
            .create(radarDependencies = radarDependencies)
            .radarViewModel()
    }