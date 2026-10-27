package fi.kroon.vadret.favorite

import android.content.Context
import androidx.room3.Room
import fi.kroon.vadret.favorite.local.FavoriteDatabase
import fi.kroon.vadret.idgenerator.IdGenerator
import kotlin.time.Clock

fun FavoriteRepository(
    context: Context,
    idGenerator: IdGenerator,
    clock: Clock
): FavoriteRepository =
    FavoriteRepository(
        databaseBuilder = Room.databaseBuilder<FavoriteDatabase>(
            context = context,
            name = FAVORITE_DATABASE_NAME
        ),
        idGenerator = idGenerator,
        clock = clock
    )