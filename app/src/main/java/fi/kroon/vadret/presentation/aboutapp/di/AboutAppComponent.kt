package fi.kroon.vadret.presentation.aboutapp.di

import androidx.fragment.app.FragmentManager
import dagger.BindsInstance
import dagger.Subcomponent
import fi.kroon.vadret.presentation.aboutapp.AboutAppFragment
import fi.kroon.vadret.presentation.aboutapp.about.AboutAppAboutFragment
import fi.kroon.vadret.presentation.aboutapp.about.AboutAppAboutModule
import fi.kroon.vadret.presentation.aboutapp.library.AboutAppLibraryFragment
import fi.kroon.vadret.presentation.aboutapp.library.AboutAppLibraryModule

@Subcomponent(
    modules = [
        AboutAppModule::class,
        AboutAppAboutModule::class,
        AboutAppLibraryModule::class
    ]
)
@AboutAppFeatureScope
interface AboutAppComponent {

    fun inject(aboutAppFragment: AboutAppFragment)
    fun inject(aboutAppAboutAppFragment: AboutAppAboutFragment)
    fun inject(aboutAppLibraryFragment: AboutAppLibraryFragment)

    @Subcomponent.Builder
    interface Builder {

        @BindsInstance
        fun fragmentManager(fragmentManager: FragmentManager): Builder

        fun aboutAppModule(module: AboutAppModule): Builder
        fun aboutAppAboutModule(module: AboutAppAboutModule): Builder
        fun aboutAppLibraryModule(module: AboutAppLibraryModule): Builder

        fun build(): AboutAppComponent
    }
}