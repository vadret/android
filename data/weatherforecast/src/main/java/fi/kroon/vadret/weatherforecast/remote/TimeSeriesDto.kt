package fi.kroon.vadret.weatherforecast.remote

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class TimeSeriesDto(
    @SerialName(value = "time")
    val time: String,
    @SerialName(value = "data")
    val data: ForecastDataDto
)