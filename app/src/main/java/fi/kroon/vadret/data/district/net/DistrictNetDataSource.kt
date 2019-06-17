package fi.kroon.vadret.data.district.net

import fi.kroon.vadret.data.district.model.DistrictView
import fi.kroon.vadret.util.DISTRICT_VIEW_FILTER
import fi.kroon.vadret.util.SMHI_BASE_API_VERSION
import io.reactivex.Single
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface DistrictNetDataSource {

    @GET("api/version/{version}/districtviews/{type}.json")
    operator fun invoke(
        @Path("type") type: String = DISTRICT_VIEW_FILTER,
        @Path("version") version: Int = SMHI_BASE_API_VERSION
    ): Single<Response<DistrictView>>
}