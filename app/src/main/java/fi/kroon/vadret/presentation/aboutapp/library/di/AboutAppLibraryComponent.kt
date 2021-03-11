package fi.kroon.vadret.presentation.aboutapp.library.di

import android.content.Context
import dagger.BindsInstance
import dagger.Component
import fi.kroon.vadret.presentation.aboutapp.library.AboutAppLibraryViewModel

@AboutAppLibraryScope
@Component(
    modules = [
        AboutAppLibraryModule::class
    ]
)
interface AboutAppLibraryComponent {

    fun provideViewModel(): AboutAppLibraryViewModel

    @Component.Factory
    interface Factory {
        fun create(
            @BindsInstance
            context: Context
        ): AboutAppLibraryComponent
    }
}