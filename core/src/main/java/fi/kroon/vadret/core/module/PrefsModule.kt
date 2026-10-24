package fi.kroon.vadret.core.module

import android.content.Context
import dagger.Module
import dagger.Provides
import fi.kroon.vadret.core.CoreScope
import fi.kroon.vadret.data.preference.Prefs
import fi.kroon.vadret.util.DEFAULT_SETTINGS

@Module
object PrefsModule {

    @Provides
    @CoreScope
    fun providePrefs(context: Context): Prefs =
        Prefs(context.getSharedPreferences(DEFAULT_SETTINGS, Context.MODE_PRIVATE))
}