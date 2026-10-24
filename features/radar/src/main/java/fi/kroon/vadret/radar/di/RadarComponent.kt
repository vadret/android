package fi.kroon.vadret.radar.di

import android.content.Context
import coil.ImageLoader
import dagger.BindsInstance
import dagger.Component
import fi.kroon.vadret.core.CoreComponent
import fi.kroon.vadret.radar.RadarViewModel

@Component(
    modules = [
        RadarModule::class
    ],
    dependencies = [
        CoreComponent::class
    ]
)
@RadarScope
interface RadarComponent {

    /**
     *  ViewModel
     */
    fun provideRadarViewModel(): RadarViewModel

    fun provideImageLoader(): ImageLoader

    @Component.Factory
    interface Factory {
        fun create(
            @BindsInstance
            context: Context,
            coreComponent: CoreComponent
        ): RadarComponent
    }
}