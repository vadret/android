package fi.kroon.vadret.aggregatedfeed.remote

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.expectSuccess
import io.ktor.client.request.get
import io.ktor.client.request.parameter

internal class DefaultKrisinformationRemoteDataSource(
    private val httpClient: Lazy<HttpClient>
) : KrisinformationRemoteDataSource {

    override suspend fun getNews(counties: String): List<KrisinformationFeedDto> =
        httpClient.value.get(urlString = "https://api.krisinformation.se/v3/news") {
            expectSuccess = true
            parameter(key = "counties", value = counties)
        }.body()

    override suspend fun getVmas(counties: String): List<KrisinformationFeedDto> =
        httpClient.value.get(urlString = "https://api.krisinformation.se/v3/vmas") {
            expectSuccess = true
            parameter(key = "counties", value = counties)
        }.body()
}