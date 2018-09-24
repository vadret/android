package fi.kroon.vadret.data.radar.net

import fi.kroon.vadret.data.radar.model.Radar
import io.reactivex.Single
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface RadarApi {

    @GET("/api/version/latest/area/sweden/product/comp/{year}/{month}/{date}")
    fun get(
        @Path("year") year: String,
        @Path("month") month: String,
        @Path("date") date: String,
        @Query("format") format: String,
        @Query("timeZone") timeZone: String
    ): Single<Response<Radar>>
}