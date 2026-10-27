package fi.kroon.vadret.warningfilter.local

internal interface WarningFilterLocalDataSource {
    suspend fun countDistricts(): Int
    suspend fun getDistricts(): List<DistrictEntity>
    suspend fun insertDistricts(districtEntities: List<DistrictEntity>)
    suspend fun getFeedSourcePreferences(): List<FeedSourcePreferenceEntity>

    suspend fun setWarningFilter(
        enabledDistrictIds: List<Int>,
        feedSourcePreferenceEntities: List<FeedSourcePreferenceEntity>
    )
}