package fi.kroon.vadret.settings.about.di

import android.content.Context
import dagger.BindsInstance
import dagger.Component
import fi.kroon.vadret.settings.about.AboutAppAboutViewModel

@AboutAppAboutScope
@Component(
    modules = [
        AboutAppAboutModule::class
    ]
)
interface AboutAppAboutComponent {

    fun provideViewModel(): AboutAppAboutViewModel

    @Component.Factory
    interface Factory {
        fun create(
            @BindsInstance
            context: Context
        ): AboutAppAboutComponent
    }
}