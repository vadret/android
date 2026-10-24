package fi.kroon.vadret.di

import dagger.Module
import dagger.Provides
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

@Module
object DispatcherModule {

    /**
     *  State and blocking reads run on this dispatcher,
     *  tests replace it with a test dispatcher.
     */
    @Provides
    fun provideCoroutineDispatcher(): CoroutineDispatcher = Dispatchers.IO
}