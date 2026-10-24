package fi.kroon.vadret.data.nominatim.net

import fi.kroon.vadret.data.nominatim.model.Nominatim
import fi.kroon.vadret.util.NOMINATIM_BASE_API_URL
import fi.kroon.vadret.util.extension.bodyOrNull
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import javax.inject.Inject

class NominatimNetDataSource @Inject constructor(
    private val client: HttpClient
) {

    suspend fun getNominatim(
        city: String,
        format: String,
        countryCodes: String,
        limit: Int,
        nameDetails: Int,
        addressDetails: Int
    ): List<Nominatim>? =
        client.get("${NOMINATIM_BASE_API_URL}search") {
            parameter("city", city)
            parameter("format", format)
            parameter("countrycodes", countryCodes)
            parameter("limit", limit)
            parameter("namedetails", nameDetails)
            parameter("addressdetails", addressDetails)
        }.bodyOrNull()

    suspend fun getNominatimReverse(
        format: String,
        latitude: Double,
        longitude: Double,
        zoom: Int
    ): Nominatim? =
        client.get("${NOMINATIM_BASE_API_URL}reverse") {
            parameter("format", format)
            parameter("lat", latitude)
            parameter("lon", longitude)
            parameter("zoom", zoom)
        }.bodyOrNull()
}