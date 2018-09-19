package fi.kroon.vadret.di.modules

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import dagger.Module
import dagger.Provides
import fi.kroon.vadret.data.PREFERENCES
import fi.kroon.vadret.di.scope.VadretApplicationScope

@Module
class SharedPreferencesModule {

    @Module
    companion object {
        @Provides
        @JvmStatic
        @VadretApplicationScope
        fun sharedPreferences(context: Context): SharedPreferences {
            val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
            val editor = sharedPreferences.edit()
            editor.apply()
            return context.getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE)
        }
    }
}