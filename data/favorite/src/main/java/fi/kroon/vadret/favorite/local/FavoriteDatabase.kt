package fi.kroon.vadret.favorite.local

import androidx.room3.Database
import androidx.room3.RoomDatabase

private const val FAVORITE_DATABASE_VERSION = 1

@Database(
    entities = [FavoriteEntity::class],
    version = FAVORITE_DATABASE_VERSION,
    exportSchema = false
)
internal abstract class FavoriteDatabase : RoomDatabase() {
    abstract fun favoriteDao(): FavoriteDao
}