package fi.kroon.vadret.favorite

import android.content.Context
import androidx.room3.Room
import fi.kroon.vadret.common.Failure
import fi.kroon.vadret.favorite.local.DefaultFavoriteLocalDataSource
import fi.kroon.vadret.favorite.local.FavoriteDatabase
import fi.kroon.vadret.favorite.model.Favorite
import fi.kroon.vadret.idgenerator.IdGenerator
import io.github.sphrak.either.Either
import kotlinx.coroutines.flow.Flow
import kotlin.time.Clock

private const val DATABASE_NAME = "favorite.db"

interface FavoriteRepository {

    fun observeFavorites(): Flow<Either<Failure, List<Favorite>>>

    suspend fun addFavorite(
        locality: String,
        municipality: String,
        county: String,
        latitude: Double,
        longitude: Double
    ): Either<Failure, Favorite>

    suspend fun removeFavorite(favoriteId: String): Either<Failure, Unit>
}

fun FavoriteRepository(
    context: Context,
    idGenerator: IdGenerator,
    clock: Clock
): FavoriteRepository =
    DefaultFavoriteRepository(
        favoriteLocalDataSource = DefaultFavoriteLocalDataSource(
            favoriteDatabase = Room
                .databaseBuilder<FavoriteDatabase>(
                    context = context,
                    name = DATABASE_NAME
                )
                .build()
        ),
        idGenerator = idGenerator,
        clock = clock
    )