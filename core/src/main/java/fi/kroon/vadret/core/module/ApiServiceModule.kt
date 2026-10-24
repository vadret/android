package fi.kroon.vadret.core.module

import com.squareup.moshi.Moshi
import dagger.Module
import dagger.Provides
import fi.kroon.vadret.core.CoreScope
import fi.kroon.vadret.data.common.SingleToArrayAdapter

@Module
object ApiServiceModule {

    @Provides
    @CoreScope
    fun provideMoshi(): Moshi = Moshi
        .Builder()
        .add(SingleToArrayAdapter.INSTANCE)
        .build()
}