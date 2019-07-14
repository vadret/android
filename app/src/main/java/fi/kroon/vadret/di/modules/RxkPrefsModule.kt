package fi.kroon.vadret.di.modules

import android.content.Context
import com.afollestad.rxkprefs.RxkPrefs
import com.afollestad.rxkprefs.rxkPrefs
import dagger.Module
import dagger.Provides
import fi.kroon.vadret.data.exception.ErrorHandler
import fi.kroon.vadret.di.scope.CoreApplicationScope
import fi.kroon.vadret.util.DEFAULT_SETTINGS

@Module
object RxkPrefsModule {

    @Provides
    @JvmStatic
    @CoreApplicationScope
    fun provideRxkPrefs(context: Context): RxkPrefs = rxkPrefs(context, DEFAULT_SETTINGS)

    @Provides
    @JvmStatic
    @CoreApplicationScope
    fun provideFailureHandler(): ErrorHandler = ErrorHandler()
}