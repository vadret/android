package fi.kroon.vadret.data.district.net

import fi.kroon.vadret.data.district.model.District
import retrofit2.Response
import retrofit2.http.GET

interface DistrictNetDataSource {

    @GET("ibww/api/version/1/metadata/area.json")
    suspend operator fun invoke(): Response<List<District>>
}