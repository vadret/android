package fi.kroon.vadret.data.district

import fi.kroon.vadret.data.district.model.District
import fi.kroon.vadret.data.district.model.DistrictEntity

object DistrictEntityMapper {

    /**
     *  Transforms [District] into it's database
     *  representation [DistrictEntity].
     */
    operator fun invoke(districtViewList: List<District>): List<DistrictEntity> =
        districtViewList.map { districtView: District ->
            with(districtView) {
                DistrictEntity(
                    id = id.toInt(),
                    sortOrder = sortOrder,
                    category = category,
                    name = name.replace(",", ", ")
                )
            }
        }
}