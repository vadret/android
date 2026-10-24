package fi.kroon.vadret.data.districtpreference

import fi.kroon.vadret.data.districtpreference.local.DistrictPreferenceDao
import fi.kroon.vadret.data.districtpreference.model.DistrictPreferenceEntity
import fi.kroon.vadret.data.failure.Failure
import fi.kroon.vadret.util.extension.asRight
import io.github.sphrak.either.Either
import javax.inject.Inject

class DistrictPreferenceRepository @Inject constructor(
    private val dao: DistrictPreferenceDao
) {
    suspend fun getAll(): Either<Failure, List<DistrictPreferenceEntity>> =
        dao.getAll()
            .asRight()

    suspend fun put(entity: DistrictPreferenceEntity): Either<Nothing, Long> =
        dao.insert(entity = entity)
            .asRight()

    suspend fun put(entityList: List<DistrictPreferenceEntity>): Either<Failure, List<Long>> =
        dao.insert(entityList = entityList)
            .asRight()

    suspend fun deleteAll(): Either<Failure, Unit> =
        dao.deleteAll()
            .asRight()

    suspend fun deleteAllEnabledUsedBy(usedBy: String): Either<Failure, Unit> =
        dao.deleteAllEnabledUsedBy(usedBy = usedBy)
            .asRight()
}