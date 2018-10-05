package fi.kroon.vadret.data.weather.net

import fi.kroon.vadret.data.HEADER_CACHE_CONTROL
import fi.kroon.vadret.data.weather.model.Weather
import io.reactivex.Single
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path

interface WeatherApi {

    /**
     * longlat 10 day report
     */
    @GET("/api/category/{category}/version/{version}/geotype/point/lon/{longitude}/lat/{latitude}/data.json")
    fun get(
        @Path("category") category: String,
        @Path("version") version: Int,
        @Path("longitude") longitude: Double,
        @Path("latitude") latitude: Double,
        @Header(HEADER_CACHE_CONTROL) cacheControl: String? = null
    ): Single<Response<Weather>>

    /**
     *  Areaanalyser
     */
    @GET("/api/category/mesan1g/version/1/geotype/multipoint/validtime/{validTime}/parameter/{p}/leveltype/{levelType}/level/{level}/data.json?with-geo=false")
    fun get(
        @Path("validTime") validTime: String,
        @Path("p") p: String,
        @Path("levelType") levelType: String,
        @Path("level") level: String,
        @Header(HEADER_CACHE_CONTROL) cacheControl: String? = null
    ): Single<Response<Weather>>

    /**
     *  Punktanalyser
     */
    @GET("/api/category/mesan1g/version/2/geotype/point/lon/{longitude}/lat/{latitude}/data.json")
    fun get(
        @Path("longitude") longitude: Double,
        @Path("latitude") latitude: Double,
        @Header(HEADER_CACHE_CONTROL) cacheControl: String? = null
    )
}