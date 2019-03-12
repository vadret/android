package fi.kroon.vadret.core.di.modules

import android.app.Application
import android.content.Context
import dagger.Module
import dagger.Provides
import fi.kroon.vadret.core.di.scope.VadretApplicationScope

@Module
class ContextModule(private val application: Application) {

    @Provides
    @VadretApplicationScope
    fun provideContext(): Context = application
}