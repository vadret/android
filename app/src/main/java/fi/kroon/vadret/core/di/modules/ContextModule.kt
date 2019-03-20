package fi.kroon.vadret.core.di.modules

import android.app.Application
import android.content.Context
import dagger.Module
import dagger.Provides
import fi.kroon.vadret.core.di.scope.CoreApplicationScope

@Module
class ContextModule(private val application: Application) {

    @Provides
    @CoreApplicationScope
    fun provideContext(): Context = application
}