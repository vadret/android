package fi.kroon.vadret.domain.district

import fi.kroon.vadret.data.district.DistrictEntityMapper
import fi.kroon.vadret.data.district.local.DistrictDao
import fi.kroon.vadret.data.district.model.District
import fi.kroon.vadret.data.district.model.DistrictEntity
import fi.kroon.vadret.data.exception.Failure
import fi.kroon.vadret.util.extension.asRight
import io.github.sphrak.either.Either
import io.reactivex.Single
import javax.inject.Inject

class SetDistrictListTask @Inject constructor(
    private val dao: DistrictDao
) {
    operator fun invoke(districtList: List<District>): Single<Either<Failure, List<Long>>> {
        val districtEntityList: List<DistrictEntity> = DistrictEntityMapper(districtList)
        return dao
            .insert(entityList = districtEntityList)
            .map { ids: List<Long> ->
                ids.asRight()
            }
    }
}