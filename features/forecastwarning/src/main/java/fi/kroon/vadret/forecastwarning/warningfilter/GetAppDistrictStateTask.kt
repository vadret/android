package fi.kroon.vadret.forecastwarning.warningfilter

import fi.kroon.vadret.data.district.model.DistrictOptionEntity
import fi.kroon.vadret.data.districtpreference.local.DistrictPreferenceDao
import fi.kroon.vadret.data.failure.Failure
import fi.kroon.vadret.util.APP_WARNING_FILTER_KEY
import fi.kroon.vadret.util.extension.asRight
import io.github.sphrak.either.Either
import javax.inject.Inject

class GetAppDistrictStateTask @Inject constructor(
    private val dao: DistrictPreferenceDao
) {
    suspend operator fun invoke(): Either<Failure, List<DistrictOptionEntity>> =
        dao.getAllUsedBy(APP_WARNING_FILTER_KEY)

            .let { districtOptionList: List<DistrictOptionEntity> ->
                districtOptionList
                    .sortedWith(
                        compareBy { districtOptionEntity: DistrictOptionEntity ->
                            districtOptionEntity.name
                        }
                    ).asRight()
            }
}