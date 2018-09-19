package fi.kroon.vadret.di.modules

import dagger.Module
import dagger.Provides
import fi.kroon.vadret.di.scope.VadretApplicationScope
import fi.kroon.vadret.utils.Schedulers

@Module
class SchedulerModule {

    @Module
    companion object {
        @Provides
        @JvmStatic
        @VadretApplicationScope
        fun schedulers() = Schedulers()
    }
}