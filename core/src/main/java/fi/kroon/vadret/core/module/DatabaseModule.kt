package fi.kroon.vadret.core.module

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import fi.kroon.vadret.core.CoreScope
import fi.kroon.vadret.data.persistance.AppDatabase
import fi.kroon.vadret.util.DATABASE_NAME

@Module
object DatabaseModule {

    @Provides
    @CoreScope
    fun provideAppDatabase(
        context: Context
    ): AppDatabase = Room.databaseBuilder(
        context,
        AppDatabase::class.java,
        DATABASE_NAME
    ).build()
}