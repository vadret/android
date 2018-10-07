package fi.kroon.vadret.data.weather

import fi.kroon.vadret.data.BASE_API_VERSION
import fi.kroon.vadret.data.HELSINGBORG_LON
import fi.kroon.vadret.data.HELSINGBORG_LAT
import fi.kroon.vadret.data.PMP3G_CATEGORY
import fi.kroon.vadret.data.POINT_GEOTYPE

data class WeatherRequest(
    val version: Int = BASE_API_VERSION,
    val category: String = PMP3G_CATEGORY,
    val geotype: String = POINT_GEOTYPE,
    val longitude: Double = HELSINGBORG_LON,
    val latitude: Double = HELSINGBORG_LAT
)