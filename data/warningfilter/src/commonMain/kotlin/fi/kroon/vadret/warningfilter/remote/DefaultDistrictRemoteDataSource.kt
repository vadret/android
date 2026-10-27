package fi.kroon.vadret.warningfilter.remote

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.expectSuccess
import io.ktor.client.request.get

internal class DefaultDistrictRemoteDataSource(
    private val httpClient: Lazy<HttpClient>
) : DistrictRemoteDataSource {

    override suspend fun getDistricts(): List<DistrictDto> =
        httpClient.value.get(urlString = "https://opendata-download-warnings.smhi.se/ibww/api/version/1/metadata/area.json") {
            expectSuccess = true
        }.body()
}