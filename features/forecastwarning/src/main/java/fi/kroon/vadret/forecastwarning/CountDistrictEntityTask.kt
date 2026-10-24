package fi.kroon.vadret.forecastwarning

import fi.kroon.vadret.data.district.local.DistrictDao
import fi.kroon.vadret.data.failure.Failure
import fi.kroon.vadret.util.extension.asRight
import io.github.sphrak.either.Either
import javax.inject.Inject

class CountDistrictEntityTask @Inject constructor(
    private val dao: DistrictDao
) {
    suspend operator fun invoke(): Either<Failure, Boolean> =
        dao.count()
            .let { rowCount: Int ->
                (rowCount > 0).asRight()
            }
}