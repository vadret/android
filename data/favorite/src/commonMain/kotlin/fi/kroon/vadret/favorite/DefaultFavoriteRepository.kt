package fi.kroon.vadret.favorite

import fi.kroon.vadret.common.Failure
import fi.kroon.vadret.common.catch
import fi.kroon.vadret.common.either
import fi.kroon.vadret.favorite.extension.toFailure
import fi.kroon.vadret.favorite.extension.toFavorite
import fi.kroon.vadret.favorite.local.FavoriteEntity
import fi.kroon.vadret.favorite.local.FavoriteLocalDataSource
import fi.kroon.vadret.favorite.model.Favorite
import fi.kroon.vadret.idgenerator.IdGenerator
import io.github.sphrak.either.Either
import io.github.sphrak.either.extension.asLeft
import io.github.sphrak.either.extension.asRight
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlin.time.Clock

internal class DefaultFavoriteRepository(
    private val favoriteLocalDataSource: FavoriteLocalDataSource,
    private val idGenerator: IdGenerator,
    private val clock: Clock
) : FavoriteRepository {

    override fun observeFavorites(): Flow<Either<Failure, List<Favorite>>> =
        favoriteLocalDataSource
            .observeFavorites()
            .map<List<FavoriteEntity>, Either<Failure, List<Favorite>>> { favoriteEntities: List<FavoriteEntity> ->
                favoriteEntities
                    .map { favoriteEntity: FavoriteEntity -> favoriteEntity.toFavorite() }
                    .asRight()
            }.catch { throwable: Throwable ->
                if (throwable is Exception) {
                    emit(throwable.toFailure().asLeft())
                } else {
                    throw throwable
                }
            }

    override suspend fun addFavorite(
        locality: String,
        municipality: String,
        county: String,
        latitude: Double,
        longitude: Double
    ): Either<Failure, Favorite> =
        either {
            favoriteLocalDataSource
                .addFavorite(
                    favoriteEntity = FavoriteEntity(
                        favoriteId = idGenerator.generate(),
                        locality = locality,
                        municipality = municipality,
                        county = county,
                        latitude = latitude,
                        longitude = longitude,
                        createdAt = clock.now().toEpochMilliseconds()
                    )
                ).toFavorite()
        }.catch { exception: Exception -> exception.toFailure() }

    override suspend fun removeFavorite(favoriteId: String): Either<Failure, Unit> =
        either { favoriteLocalDataSource.removeFavorite(favoriteId = favoriteId) }
            .catch { exception: Exception -> exception.toFailure() }
}