package fi.kroon.vadret.settings.di

import android.content.Context
import dagger.BindsInstance
import dagger.Component
import fi.kroon.vadret.settings.AboutAppViewModel
import fi.kroon.vadret.settings.about.di.AboutAppAboutScope

@AboutAppAboutScope
@Component(
    modules = [
        AboutAppModule::class
    ]
)
interface AboutAppComponent {

    fun provideViewModel(): AboutAppViewModel

    @Component.Factory
    interface Factory {
        fun create(
            @BindsInstance
            context: Context
        ): AboutAppComponent
    }
}