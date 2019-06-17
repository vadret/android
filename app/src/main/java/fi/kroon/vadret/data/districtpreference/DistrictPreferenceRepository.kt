package fi.kroon.vadret.data.districtpreference

import fi.kroon.vadret.data.districtpreference.local.DistrictPreferenceDao
import fi.kroon.vadret.data.districtpreference.model.DistrictPreferenceEntity
import fi.kroon.vadret.data.exception.Failure
import fi.kroon.vadret.data.functional.Either
import fi.kroon.vadret.util.extension.asRight
import io.reactivex.Single
import javax.inject.Inject

class DistrictPreferenceRepository @Inject constructor(
    private val dao: DistrictPreferenceDao
) {
    fun getAll(): Single<Either<Failure, List<DistrictPreferenceEntity>>> =
        dao.getAll()
            .map { entityList: List<DistrictPreferenceEntity> ->
                entityList
                    .asRight()
            }

    fun put(entity: DistrictPreferenceEntity): Single<Either.Right<Long>> =
        dao.insert(entity = entity)
            .map { id: Long ->
                id.asRight()
            }

    fun put(entityList: List<DistrictPreferenceEntity>): Single<Either<Failure, List<Long>>> =
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