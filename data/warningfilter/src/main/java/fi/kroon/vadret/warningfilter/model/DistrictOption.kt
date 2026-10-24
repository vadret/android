package fi.kroon.vadret.warningfilter.model

data class DistrictOption(
    val districtId: Int,
    val name: String,
    val category: DistrictCategory,
    val isEnabled: Boolean
)