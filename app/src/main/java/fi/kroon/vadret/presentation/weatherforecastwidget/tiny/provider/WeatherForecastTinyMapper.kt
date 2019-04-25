package fi.kroon.vadret.presentation.weatherforecastwidget.tiny.provider

import fi.kroon.vadret.data.weatherforecast.model.Parameter
import fi.kroon.vadret.data.weatherforecast.model.TimeSerie
import fi.kroon.vadret.presentation.weatherforecastwidget.tiny.provider.model.WeatherForecastTinyModel

object WeatherForecastTinyMapper {

    operator fun invoke(timeSerie: TimeSerie, localityName: String?): List<WeatherForecastTinyModel> {
        var temperature: Double? = null

        timeSerie.parameters.forEach { parameter: Parameter ->
            when (parameter.name) {
                "t" -> temperature = parameter.values.first()
            }
        }

        return listOf(
            WeatherForecastTinyModel(
                localityName = localityName,
                temperature = temperature
            )
        )
    }
}