package fi.kroon.vadret.domain.warning

import fi.kroon.vadret.data.districtpreference.local.DistrictPreferenceDao
import fi.kroon.vadret.data.exception.Failure
import fi.kroon.vadret.data.functional.Either
import fi.kroon.vadret.util.APP_WARNING_FILTER_KEY
import fi.kroon.vadret.util.DISTRICT_MAX
import fi.kroon.vadret.util.extension.asRight
import io.reactivex.Single
import timber.log.Timber
import javax.inject.Inject

class CountDistrictPreferenceEntityTask @Inject constructor(
    private val dao: DistrictPreferenceDao
) {
    operator fun invoke(): Single<Either<Failure, Boolean>> =
        dao.count(usedBy = APP_WARNING_FILTER_KEY)
            .map { rowCount: Int ->
                Timber.d("ROW COUNT: $rowCount")
                (rowCount == DISTRICT_MAX).asRight()
            }
}