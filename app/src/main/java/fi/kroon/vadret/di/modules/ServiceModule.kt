package fi.kroon.vadret.di.modules

import dagger.Binds
import dagger.Module
import fi.kroon.vadret.di.scope.CoreApplicationScope
import fi.kroon.vadret.util.common.DateTimeUtil
import fi.kroon.vadret.util.common.IDateTimeUtil

@Module
abstract class ServiceModule {

    @Binds
    @CoreApplicationScope
    abstract fun bindsDateTimeUtil(dateTimeUtil: DateTimeUtil): IDateTimeUtil
}