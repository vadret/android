package fi.kroon.vadret.data.feedsourcepreference

import fi.kroon.vadret.data.exception.Failure
import fi.kroon.vadret.data.feedsourcepreference.local.FeedSourcePreferenceDao
import fi.kroon.vadret.data.feedsourcepreference.model.FeedSourcePreferenceEntity
import fi.kroon.vadret.data.functional.Either
import fi.kroon.vadret.util.extension.asRight
import io.reactivex.Single
import javax.inject.Inject

class FeedSourcePreferenceRepository @Inject constructor(
    private val dao: FeedSourcePreferenceDao
) {
    fun getAll(): Single<Either<Failure, List<FeedSourcePreferenceEntity>>> =
        dao.getAll()
            .map { entityList: List<FeedSourcePreferenceEntity> ->
                entityList
                    .asRight()
            }

    fun insert(entity: FeedSourcePreferenceEntity): Single<Either<Failure, Long>> =
        dao.insert(entity = entity)
            .map { id: Long ->
                id.asRight()
            }

    fun insert(entityList: List<FeedSourcePreferenceEntity>): Single<Either<Failure, List<Long>>> =
        dao.insert(entityList = entityList)
            .map { ids: List<Long> ->
                ids.asRight()
            }

    fun deleteAll(): Single<Either<Failure, Unit>> = dao
        .deleteAll()
        .map { unit: Unit ->
            unit.asRight()
        }

    fun deleteAllEnabledUsedBy(usedBy: String): Single<Either<Failure, Unit>> =
        dao.deleteAllEnabledUsedBy(usedBy = usedBy)
            .map { unit: Unit ->
                unit.asRight()
            }
}