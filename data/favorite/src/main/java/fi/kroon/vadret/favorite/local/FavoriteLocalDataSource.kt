package fi.kroon.vadret.favorite.local

import kotlinx.coroutines.flow.Flow

internal interface FavoriteLocalDataSource {
    fun observeFavorites(): Flow<List<FavoriteEntity>>
    suspend fun addFavorite(favoriteEntity: FavoriteEntity): FavoriteEntity
    suspend fun removeFavorite(favoriteId: String)
}