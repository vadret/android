package fi.kroon.vadret.forecastwarning.district

import fi.kroon.vadret.data.district.local.DistrictDao
import fi.kroon.vadret.data.district.mapper.DistrictEntityMapper
import fi.kroon.vadret.data.district.model.District
import fi.kroon.vadret.data.district.model.DistrictEntity
import fi.kroon.vadret.data.failure.Failure
import fi.kroon.vadret.util.extension.asRight
import io.github.sphrak.either.Either
import javax.inject.Inject

class SetDistrictListTask @Inject constructor(
    private val dao: DistrictDao
) {
    suspend operator fun invoke(districtList: List<District>): Either<Failure, List<Long>> {
        val districtEntityList: List<DistrictEntity> = DistrictEntityMapper(districtList)
        return dao
            .insert(entityList = districtEntityList)
            .let { ids: List<Long> ->
                ids.asRight()
            }
    }
}