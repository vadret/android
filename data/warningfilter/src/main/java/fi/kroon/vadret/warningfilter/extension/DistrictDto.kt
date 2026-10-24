package fi.kroon.vadret.warningfilter.extension

import fi.kroon.vadret.warningfilter.local.DistrictEntity
import fi.kroon.vadret.warningfilter.remote.DistrictDto

internal fun DistrictDto.toDistrictEntity(isEnabled: Boolean): DistrictEntity =
    DistrictEntity(
        districtId = id,
        name = name,
        code = code,
        isEnabled = isEnabled
    )