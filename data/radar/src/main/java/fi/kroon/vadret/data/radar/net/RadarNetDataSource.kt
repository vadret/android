package fi.kroon.vadret.data.radar.net

import fi.kroon.vadret.data.radar.model.Radar
import fi.kroon.vadret.util.SMHI_API_RADAR_URL
import fi.kroon.vadret.util.extension.bodyOrNull
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.parameter
import io.ktor.http.HttpHeaders
import javax.inject.Inject

class RadarNetDataSource @Inject constructor(
    private val client: HttpClient
) {

    suspend fun getRadar(
        year: String,
        month: String,
        date: String,
        format: String,
        timeZone: String
    ): Radar? =
        client.get("$SMHI_API_RADAR_URL/api/version/latest/area/sweden/product/comp/$year/$month/$date") {
            header(HttpHeaders.CacheControl, "public, max-stale=2419200")
            parameter("format", format)
            parameter("timeZone", timeZone)
        }.bodyOrNull()
}