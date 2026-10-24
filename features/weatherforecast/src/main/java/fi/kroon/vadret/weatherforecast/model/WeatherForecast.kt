package fi.kroon.vadret.weatherforecast.model

/**
 *  @param overview the headline and summary of the current hour,
 *  hidden while searching.
 *  @param days a header per local date followed by its hours.
 */
data class WeatherForecast(
    val overview: List<WeatherForecastRow>,
    val days: List<WeatherForecastRow>
)