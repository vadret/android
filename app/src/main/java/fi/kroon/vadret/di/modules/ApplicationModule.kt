package fi.kroon.vadret.di.modules

import android.app.Application
import android.content.Context
import dagger.Module
import dagger.Provides
import fi.kroon.vadret.di.scope.VadretApplicationScope

@Module
class ApplicationModule(val application: Application) {
    @Provides
    @VadretApplicationScope
    fun context(): Context = application
}