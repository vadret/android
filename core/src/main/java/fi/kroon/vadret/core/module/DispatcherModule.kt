package fi.kroon.vadret.core.module

import dagger.Module
import dagger.Provides
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

@Module
object DispatcherModule {

    /**
     *  View models produce their state on this dispatcher,
     *  tests replace it with a test dispatcher.
     */
    @Provides
    fun provideCoroutineDispatcher(): CoroutineDispatcher = Dispatchers.IO
}