package fi.kroon.vadret.di.modules

import android.app.Application
import android.content.Context
import dagger.Module
import dagger.Provides
import fi.kroon.vadret.di.scope.CoreApplicationScope

@Module
class ContextModule(private val application: Application) {

    @Provides
    @CoreApplicationScope
    fun provideContext(): Context = application
}