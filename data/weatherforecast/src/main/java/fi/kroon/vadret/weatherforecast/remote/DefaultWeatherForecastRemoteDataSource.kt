package fi.kroon.vadret.weatherforecast.remote

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.expectSuccess
import io.ktor.client.request.get
import java.math.BigDecimal
import java.math.RoundingMode

/**
 *  SMHI rejects coordinates with more than six decimals.
 */
private const val COORDINATE_DECIMALS = 6

internal class DefaultWeatherForecastRemoteDataSource(
    private val httpClient: Lazy<HttpClient>
) : WeatherForecastRemoteDataSource {

    override suspend fun getWeatherForecast(
        latitude: Double,
        longitude: Double
    ): WeatherForecastDto {
        val formattedLongitude: String = longitude.toCoordinate()
        val formattedLatitude: String = latitude.toCoordinate()
        return httpClient.value.get(urlString = "https://opendata-download-metfcst.smhi.se/api/category/snow1g/version/1/geotype/point/lon/$formattedLongitude/lat/$formattedLatitude/data.json") {
            expectSuccess = true
        }.body()
    }

    private fun Double.toCoordinate(): String =
        BigDecimal(this)
            .setScale(COORDINATE_DECIMALS, RoundingMode.HALF_UP)
            .stripTrailingZeros()
            .toPlainString()
}