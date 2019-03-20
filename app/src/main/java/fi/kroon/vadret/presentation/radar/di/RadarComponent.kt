package fi.kroon.vadret.presentation.radar.di

import dagger.Subcomponent
import fi.kroon.vadret.presentation.radar.RadarFragment

@Subcomponent(
    modules = [
        RadarModule::class
    ]
)
@RadarFeatureScope
interface RadarComponent {

    fun inject(radarFragment: RadarFragment)

    @Subcomponent.Builder
    interface Builder {
        fun radarModule(module: RadarModule): Builder
        fun build(): RadarComponent
    }
}