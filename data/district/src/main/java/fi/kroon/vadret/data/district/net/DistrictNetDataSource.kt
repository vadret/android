package fi.kroon.vadret.data.district.net

import fi.kroon.vadret.data.district.model.District
import fi.kroon.vadret.util.SMHI_API_ALERT_URL
import fi.kroon.vadret.util.extension.bodyOrNull
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import javax.inject.Inject

class DistrictNetDataSource @Inject constructor(
    private val client: HttpClient
) {

    suspend operator fun invoke(): List<District>? =
        client.get("$SMHI_API_ALERT_URL/ibww/api/version/1/metadata/area.json")
            .bodyOrNull()
}