package fi.kroon.vadret.presentation.aboutapp.about.di

import android.content.Context
import dagger.BindsInstance
import dagger.Component
import fi.kroon.vadret.presentation.aboutapp.about.AboutAppAboutViewModel

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