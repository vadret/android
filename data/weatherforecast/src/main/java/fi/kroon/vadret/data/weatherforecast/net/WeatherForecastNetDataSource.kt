package fi.kroon.vadret.data.weatherforecast.net

import fi.kroon.vadret.data.weatherforecast.model.Weather
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface WeatherForecastNetDataSource {

    /**
     * Provide lat/lon for 10 day forecast
     */
    @GET("/api/category/{category}/version/{version}/geotype/point/lon/{longitude}/lat/{latitude}/data.json")
    suspend fun getWeatherForecast(
        @Path("category") category: String,
        @Path("version") version: Int,
        @Path("longitude") longitude: Double,
        @Path("latitude") latitude: Double
    ): Response<Weather>
}