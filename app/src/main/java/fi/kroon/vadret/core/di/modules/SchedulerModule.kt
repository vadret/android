package fi.kroon.vadret.core.di.modules

import dagger.Module
import dagger.Provides
import fi.kroon.vadret.core.di.scope.CoreApplicationScope
import fi.kroon.vadret.utils.Schedulers

@Module
object SchedulerModule {

    @Provides
    @JvmStatic
    @CoreApplicationScope
    fun provideSchedulers(): Schedulers = Schedulers()
}