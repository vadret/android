package fi.kroon.vadret.di.modules

import android.content.Context
import dagger.Module
import dagger.Provides
import fi.kroon.vadret.data.DEFAULT_PREFERENCES
import fi.kroon.vadret.di.scope.VadretApplicationScope

@Module
class SharedPreferencesModule {

    @Module
    companion object {
        @Provides
        @JvmStatic
        @VadretApplicationScope
        fun sharedPreferences(context: Context) = context.getSharedPreferences(DEFAULT_PREFERENCES, Context.MODE_PRIVATE)
    }
}