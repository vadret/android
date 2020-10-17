package fi.kroon.vadret.core.module

import android.content.Context
import com.afollestad.rxkprefs.RxkPrefs
import com.afollestad.rxkprefs.rxkPrefs
import dagger.Module
import dagger.Provides
import fi.kroon.vadret.core.CoreScope
import fi.kroon.vadret.util.DEFAULT_SETTINGS

@Module
object RxkPrefsModule {

    @Provides
    @CoreScope
    fun provideRxkPrefs(context: Context): RxkPrefs = rxkPrefs(context, DEFAULT_SETTINGS)
}