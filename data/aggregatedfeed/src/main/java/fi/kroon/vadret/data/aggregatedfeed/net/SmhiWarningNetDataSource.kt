package fi.kroon.vadret.data.aggregatedfeed.net

import fi.kroon.vadret.data.aggregatedfeed.model.SmhiWarning
import io.reactivex.Single
import retrofit2.Response
import retrofit2.http.GET

interface SmhiWarningNetDataSource {

    @GET("ibww/api/version/1/warning.json")
    fun getWarnings(): Single<Response<List<SmhiWarning>>>
}