package fi.kroon.vadret.domain.warning

import fi.kroon.vadret.data.district.local.DistrictDao
import fi.kroon.vadret.data.exception.Failure
import fi.kroon.vadret.util.FEED_SOURCE_MAX
import fi.kroon.vadret.util.extension.asRight
import io.github.sphrak.either.Either
import io.reactivex.Single
import javax.inject.Inject

class CountDistrictEntityTask @Inject constructor(
    private val dao: DistrictDao
) {
    operator fun invoke(): Single<Either<Failure, Boolean>> =
        dao.count()
            .map { rowCount: Int ->
                (rowCount == FEED_SOURCE_MAX).asRight()
            }
}