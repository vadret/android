package fi.kroon.vadret.data

data class Request(
    val version: Int = API_VERSION,
    val category: String = "pmp3g",
    val geoptype: String = "point",
    val longitude: Double = 12.694512,
    val latitude: Double = 56.046467
)