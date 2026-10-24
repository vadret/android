package fi.kroon.vadret.data.aggregatedfeed.net

import fi.kroon.vadret.data.aggregatedfeed.model.SmhiWarning
import fi.kroon.vadret.util.SMHI_API_ALERT_URL
import fi.kroon.vadret.util.extension.bodyOrNull
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import javax.inject.Inject

class SmhiWarningNetDataSource @Inject constructor(
    private val client: HttpClient
) {

    suspend fun getWarnings(): List<SmhiWarning>? =
        client.get("$SMHI_API_ALERT_URL/ibww/api/version/1/warning.json")
            .bodyOrNull()
}