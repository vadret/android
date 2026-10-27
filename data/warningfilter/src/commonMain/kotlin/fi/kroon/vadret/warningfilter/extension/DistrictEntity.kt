package fi.kroon.vadret.warningfilter.extension

import fi.kroon.vadret.warningfilter.local.DistrictEntity
import fi.kroon.vadret.warningfilter.model.DistrictCategory
import fi.kroon.vadret.warningfilter.model.DistrictOption

private const val COUNTY_CODE = "SWE_COUNTIES"
private const val SEA_DISTRICT_CODE = "SWE_SEA_DISTRICTS"

internal fun DistrictEntity.toDistrictOption(): DistrictOption =
    DistrictOption(
        districtId = districtId,
        name = name,
        category = when (code) {
            COUNTY_CODE -> DistrictCategory.COUNTY
            SEA_DISTRICT_CODE -> DistrictCategory.SEA_DISTRICT
            else -> DistrictCategory.OTHER
        },
        isEnabled = isEnabled
    )