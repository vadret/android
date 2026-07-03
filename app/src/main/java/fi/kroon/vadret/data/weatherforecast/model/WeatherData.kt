package fi.kroon.vadret.data.weatherforecast.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class WeatherData(

    @SerialName(value = "air_temperature")
    val airTemperature: Double,

    @SerialName(value = "wind_from_direction")
    val windFromDirection: Double,

    @SerialName(value = "wind_speed")
    val windSpeed: Double,

    @SerialName(value = "relative_humidity")
    val relativeHumidity: Int,

    @SerialName(value = "thunderstorm_probability")
    val thunderstormProbability: Int,

    @SerialName(value = "predominant_precipitation_type_at_surface")
    val precipitationType: Int,

    @SerialName(value = "probability_of_precipitation")
    val probabilityOfPrecipitation: Int,

    @SerialName(value = "precipitation_amount_min")
    val precipitationAmountMin: Double,

    @SerialName(value = "precipitation_amount_max")
    val precipitationAmountMax: Double,

    @SerialName(value = "symbol_code")
    val symbolCode: Int
)
