package fi.kroon.vadret.domain.district

import fi.kroon.vadret.data.district.local.DistrictDao
import fi.kroon.vadret.data.district.model.DistrictEntity
import fi.kroon.vadret.data.exception.Failure
import fi.kroon.vadret.util.extension.asRight
import io.github.sphrak.either.Either
import io.reactivex.Single
import javax.inject.Inject

class GetAllDistrictTask @Inject constructor(
    private val dao: DistrictDao
) {
    operator fun invoke(): Single<Either<Failure, List<DistrictEntity>>> =
        dao.getAll()
            .map { districtList: List<DistrictEntity> ->
                districtList.asRight()
            }
}