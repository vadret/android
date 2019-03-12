package fi.kroon.vadret.core.di.modules

import dagger.Module
import fi.kroon.vadret.presentation.aboutapp.di.AboutAppComponent

// FIXME fix this in the end.
@Module(
    subcomponents = [
        AboutAppComponent::class
    ]
)
object BindingModule