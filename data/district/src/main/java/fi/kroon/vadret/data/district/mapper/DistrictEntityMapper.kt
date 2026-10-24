package fi.kroon.vadret.data.district.mapper

import fi.kroon.vadret.data.district.model.District
import fi.kroon.vadret.data.district.model.DistrictEntity

object DistrictEntityMapper {

    private const val COUNTY_CODE = "SWE_COUNTIES"
    private const val SEA_DISTRICT_CODE = "SWE_SEA_DISTRICTS"
    private const val LAND_CATEGORY = "land"
    private const val SEA_CATEGORY = "sea"

    /**
     *  Transforms [District] into it's database
     *  representation [DistrictEntity], keeping
     *  the order SMHI lists them in.
     */
    operator fun invoke(districtList: List<District>): List<DistrictEntity> =
        districtList.mapIndexed { index: Int, district: District ->
            with(district) {
                DistrictEntity(
                    id = id,
                    sortOrder = index.toString(),
                    category = when (code) {
                        COUNTY_CODE -> LAND_CATEGORY
                        SEA_DISTRICT_CODE -> SEA_CATEGORY
                        else -> code
                    },
                    name = name
                )
            }
        }
}