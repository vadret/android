package fi.kroon.vadret.domain.districtpreference

import fi.kroon.vadret.data.district.model.DistrictOptionEntity
import fi.kroon.vadret.data.districtpreference.local.DistrictPreferenceDao
import fi.kroon.vadret.data.districtpreference.model.DistrictPreferenceEntity
import fi.kroon.vadret.data.exception.Failure
import fi.kroon.vadret.util.extension.asRight
import io.github.sphrak.either.Either
import io.reactivex.Single
import javax.inject.Inject
import timber.log.Timber

class UpdateDistrictPreferenceListTask @Inject constructor(
    private val dao: DistrictPreferenceDao
) {
    operator fun invoke(entityList: List<DistrictOptionEntity>): Single<Either<Failure, Int>> {

        val newEntityList: List<DistrictPreferenceEntity> =
            entityList.map { districtOptionEntity: DistrictOptionEntity ->
                with(districtOptionEntity) {
                    DistrictPreferenceEntity(
                        id = districtId,
                        districtId = id,
                        usedBy = usedBy,
                        isEnabled = isEnabled
                    )
                }
            }
        Timber.d("NEW ENTITY LIST: $newEntityList")

        return dao.update(entityList = newEntityList)
            .map { updatedRowCount: Int ->
                Timber.d("updated row count: $updatedRowCount")
                updatedRowCount.asRight()
            }
    }
}