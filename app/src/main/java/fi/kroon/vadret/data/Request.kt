package fi.kroon.vadret.data

data class Request(
    val version: Int = API_VERSION,
    val category: String = PMP3G_CATEGORY,
    val geotype: String = POINT_GEOTYPE,
    val longitude: Double = HELSINGBORG_LON,
    val latitude: Double = HELSINGBORG_LAT
)