package fi.kroon.vadret.di.modules

import dagger.Module
import dagger.Provides
import fi.kroon.vadret.di.scope.CoreApplicationScope
import fi.kroon.vadret.util.Schedulers

@Module
object SchedulerModule {

    @Provides
    @JvmStatic
    @CoreApplicationScope
    fun provideSchedulers(): Schedulers = Schedulers()
}