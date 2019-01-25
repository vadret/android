package fi.kroon.vadret.di.modules

import android.content.Context
import com.afollestad.rxkprefs.RxkPrefs
import com.afollestad.rxkprefs.rxkPrefs
import dagger.Module
import dagger.Provides
import fi.kroon.vadret.di.scope.VadretApplicationScope
import fi.kroon.vadret.utils.DEFAULT_PREFERENCES

@Module
object RxkPrefsModule {

    @Provides
    @JvmStatic
    @VadretApplicationScope
    fun provideRxkPrefs(context: Context): RxkPrefs = rxkPrefs(context, DEFAULT_PREFERENCES)
}