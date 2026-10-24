package fi.kroon.vadret.warningfilter.remote

internal interface DistrictRemoteDataSource {
    suspend fun getDistricts(): List<DistrictDto>
}