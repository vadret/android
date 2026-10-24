package fi.kroon.vadret.data.feedsourcepreference

import fi.kroon.vadret.data.failure.Failure
import fi.kroon.vadret.data.feedsourcepreference.local.FeedSourcePreferenceDao
import fi.kroon.vadret.data.feedsourcepreference.model.FeedSourcePreferenceEntity
import fi.kroon.vadret.util.extension.asRight
import io.github.sphrak.either.Either
import javax.inject.Inject

class FeedSourcePreferenceRepository @Inject constructor(
    private val dao: FeedSourcePreferenceDao
) {
    suspend fun getAll(): Either<Failure, List<FeedSourcePreferenceEntity>> =
        dao.getAll()
            .asRight()

    suspend fun insert(entity: FeedSourcePreferenceEntity): Either<Failure, Long> =
        dao.insert(entity = entity)
            .asRight()

    suspend fun insert(entityList: List<FeedSourcePreferenceEntity>): Either<Failure, List<Long>> =
        dao.insert(entityList = entityList)
            .asRight()

    suspend fun deleteAll(): Either<Failure, Unit> =
        dao.deleteAll()
            .asRight()

    suspend fun deleteAllEnabledUsedBy(usedBy: String): Either<Failure, Unit> =
        dao.deleteAllEnabledUsedBy(usedBy = usedBy)
            .asRight()
}