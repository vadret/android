package fi.kroon.vadret.weatherforecast.remote

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class AnalysisDto(
    @SerialName(value = "timeSeries")
    val timeSeries: List<AnalysisTimeSeriesDto>
)