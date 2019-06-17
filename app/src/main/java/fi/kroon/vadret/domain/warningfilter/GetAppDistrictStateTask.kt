package fi.kroon.vadret.domain.warningfilter

import fi.kroon.vadret.data.district.model.DistrictOptionEntity
import fi.kroon.vadret.data.districtpreference.local.DistrictPreferenceDao
import fi.kroon.vadret.data.exception.Failure
import fi.kroon.vadret.data.functional.Either
import fi.kroon.vadret.util.APP_WARNING_FILTER_KEY
import fi.kroon.vadret.util.extension.asRight
import io.reactivex.Single
import javax.inject.Inject

class GetAppDistrictStateTask @Inject constructor(
    private val dao: DistrictPreferenceDao
) {
    operator fun invoke(): Single<Either<Failure, List<DistrictOptionEntity>>> =
        dao.getAllUsedBy(APP_WARNING_FILTER_KEY)

            .map { districtOptionList: List<DistrictOptionEntity> ->
                districtOptionList
                    .sortedWith(
                        compareBy { districtOptionEntity: DistrictOptionEntity ->
                            districtOptionEntity.name
                        }
                    ).asRight()
            }
}