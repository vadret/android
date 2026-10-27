package fi.kroon.vadret.nominatim.remote

internal interface NominatimRemoteDataSource {
    suspend fun reverse(
        latitude: Double,
        longitude: Double
    ): NominatimDto
}