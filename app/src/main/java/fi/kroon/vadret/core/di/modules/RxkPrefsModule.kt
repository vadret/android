package fi.kroon.vadret.core.di.modules

import android.content.Context
import com.afollestad.rxkprefs.RxkPrefs
import com.afollestad.rxkprefs.rxkPrefs
import dagger.Module
import dagger.Provides
import fi.kroon.vadret.core.di.scope.CoreApplicationScope
import fi.kroon.vadret.utils.DEFAULT_SETTINGS

@Module
object RxkPrefsModule {

    @Provides
    @JvmStatic
    @CoreApplicationScope
    fun provideRxkPrefs(context: Context): RxkPrefs = rxkPrefs(context, DEFAULT_SETTINGS)
}