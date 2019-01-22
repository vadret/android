package fi.kroon.vadret.di.modules

import dagger.Module
import fi.kroon.vadret.presentation.aboutapp.di.AboutAppComponent

@Module(
    subcomponents = [
        AboutAppComponent::class
    ]
)
object BindingModule