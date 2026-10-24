package fi.kroon.vadret.data.weatherforecast.net

import fi.kroon.vadret.data.weatherforecast.model.Weather
import fi.kroon.vadret.util.SMHI_API_FORECAST_URL
import fi.kroon.vadret.util.extension.bodyOrNull
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import javax.inject.Inject

class WeatherForecastNetDataSource @Inject constructor(
    private val client: HttpClient
) {

    /**
     * Provide lat/lon for 10 day forecast
     */
    suspend fun getWeatherForecast(
        category: String,
        version: Int,
        longitude: Double,
        latitude: Double
    ): Weather? =
        client.get("$SMHI_API_FORECAST_URL/api/category/$category/version/$version/geotype/point/lon/$longitude/lat/$latitude/data.json")
            .bodyOrNull()
}