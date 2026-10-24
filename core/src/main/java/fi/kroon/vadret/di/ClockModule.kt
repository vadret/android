package fi.kroon.vadret.di

import dagger.Module
import dagger.Provides
import kotlin.time.Clock

@Module
object ClockModule {

    /**
     *  Injected so tests can pin "now".
     */
    @Provides
    fun provideClock(): Clock = Clock.System
}