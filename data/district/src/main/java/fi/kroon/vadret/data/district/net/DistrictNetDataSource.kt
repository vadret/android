package fi.kroon.vadret.data.district.net

import fi.kroon.vadret.data.district.model.District
import io.reactivex.Single
import retrofit2.Response
import retrofit2.http.GET

interface DistrictNetDataSource {

    @GET("ibww/api/version/1/metadata/area.json")
    operator fun invoke(): Single<Response<List<District>>>
}