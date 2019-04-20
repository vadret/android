package fi.kroon.vadret.presentation.main.di

import dagger.Subcomponent
import fi.kroon.vadret.presentation.main.MainActivity

// TODO RM
@Subcomponent(
    modules = [
        MainActivityModule::class
    ]
)
@MainActivityScope
interface MainActivityComponent {

    fun inject(mainActivity: MainActivity)

    @Subcomponent.Builder
    interface Builder {
        fun mainActivityModule(module: MainActivityModule): Builder
        fun build(): MainActivityComponent
    }
}