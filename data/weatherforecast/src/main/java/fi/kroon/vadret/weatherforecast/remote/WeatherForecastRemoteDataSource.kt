package fi.kroon.vadret.weatherforecast.remote

internal interface WeatherForecastRemoteDataSource {
    suspend fun getWeatherForecast(
        latitude: Double,
        longitude: Double
    ): WeatherForecastDto
}