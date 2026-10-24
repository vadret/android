package fi.kroon.vadret.forecastwarning.districtpreference

import fi.kroon.vadret.data.districtpreference.local.DistrictPreferenceDao
import fi.kroon.vadret.data.failure.Failure
import fi.kroon.vadret.util.APP_WARNING_FILTER_KEY
import fi.kroon.vadret.util.extension.asRight
import io.github.sphrak.either.Either
import javax.inject.Inject

class GetEnabledDistrictPreferenceIdsTask @Inject constructor(
    private val dao: DistrictPreferenceDao
) {
    suspend operator fun invoke(): Either<Failure, List<Int>> =
        dao.getAllEnabledIds(usedBy = APP_WARNING_FILTER_KEY)
            .let { ids: List<Int> ->
                ids.asRight()
            }
}