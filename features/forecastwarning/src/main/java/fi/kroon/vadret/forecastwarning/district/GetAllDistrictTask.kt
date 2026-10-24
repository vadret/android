package fi.kroon.vadret.forecastwarning.district

import fi.kroon.vadret.data.district.local.DistrictDao
import fi.kroon.vadret.data.district.model.DistrictEntity
import fi.kroon.vadret.data.failure.Failure
import fi.kroon.vadret.util.extension.asRight
import io.github.sphrak.either.Either
import javax.inject.Inject

class GetAllDistrictTask @Inject constructor(
    private val dao: DistrictDao
) {
    suspend operator fun invoke(): Either<Failure, List<DistrictEntity>> =
        dao.getAll()
            .let { districtList: List<DistrictEntity> ->
                districtList.asRight()
            }
}