package fi.kroon.vadret.main.di

import dagger.Module
import dagger.Provides
import fi.kroon.vadret.core.R
import fi.kroon.vadret.data.theme.model.ThemeStyles
import fi.kroon.vadret.main.MainActivityView

@Module
object MainActivityModule {

    @Provides
    @MainActivityScope
    fun provideViewState(): MainActivityView.State = MainActivityView.State()

    @Provides
    @MainActivityScope
    fun provideThemeStyles(): ThemeStyles = ThemeStyles(
        light = R.style.LIGHT,
        dark = R.style.DARK,
        amoled = R.style.AMOLED
    )
}