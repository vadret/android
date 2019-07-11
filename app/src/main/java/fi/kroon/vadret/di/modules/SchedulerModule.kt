package fi.kroon.vadret.di.modules

import dagger.Module
import dagger.Provides
import fi.kroon.vadret.di.scope.CoreApplicationScope
import fi.kroon.vadret.util.Scheduler

@Module
object SchedulerModule {

    @Provides
    @JvmStatic
    @CoreApplicationScope
    fun provideSchedulers(): Scheduler = Scheduler()
}