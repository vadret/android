package fi.kroon.vadret.data.weatherforecast.net

import fi.kroon.vadret.data.weatherforecast.model.Weather
import io.reactivex.Single
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface WeatherForecastNetDataSource {

    /**
     * Provide lat/lon for 10 day forecast
     */
    @GET("/api/category/{category}/version/{version}/geotype/point/lon/{longitude}/lat/{latitude}/data.json")
    fun get(
        @Path("category") category: String,
        @Path("version") version: Int,
        @Path("longitude") longitude: Double,
        @Path("latitude") latitude: Double
    ): Single<Response<Weather>>

    /**
     *  Areaanalyser
     */
    @GET("/api/category/mesan1g/version/1/geotype/multipoint/validtime/{validTime}/parameters/{p}/leveltype/{levelType}/level/{level}/data.json?with-geo=false")
    fun get(
        @Path("validTime") validTime: String,
        @Path("p") p: String,
        @Path("levelType") levelType: String,
        @Path("level") level: String
    ): Single<Response<Weather>>

    /**
     *  Punktanalyser
     */
    @GET("/api/category/mesan1g/version/2/geotype/point/lon/{longitude}/lat/{latitude}/data.json")
    fun get(
        @Path("longitude") longitude: Double,
        @Path("latitude") latitude: Double
    )
}