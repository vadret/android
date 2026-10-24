package fi.kroon.vadret.data.aggregatedfeed.net

import fi.kroon.vadret.data.aggregatedfeed.model.KrisinformationFeed
import fi.kroon.vadret.util.KRISINFORMATION_API_URL
import fi.kroon.vadret.util.extension.bodyOrNull
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import javax.inject.Inject

/**
 *  [counties] is a comma separated list of county codes,
 *  national items are returned regardless.
 */
class KrisinformationNetDataSource @Inject constructor(
    private val client: HttpClient
) {

    suspend fun getNews(counties: String): List<KrisinformationFeed>? =
        client.get("${KRISINFORMATION_API_URL}news") {
            parameter("counties", counties)
        }.bodyOrNull()

    suspend fun getVmas(counties: String): List<KrisinformationFeed>? =
        client.get("${KRISINFORMATION_API_URL}vmas") {
            parameter("counties", counties)
        }.bodyOrNull()
}