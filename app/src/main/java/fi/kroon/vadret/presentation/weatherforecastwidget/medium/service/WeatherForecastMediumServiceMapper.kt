package fi.kroon.vadret.presentation.weatherforecastwidget.medium.service

import fi.kroon.vadret.data.weatherforecast.model.Parameter
import fi.kroon.vadret.data.weatherforecast.model.TimeSerie
import fi.kroon.vadret.presentation.weatherforecastwidget.medium.service.model.WeatherForecastMediumServiceModel
import fi.kroon.vadret.utils.common.WeatherForecastUtil

object WeatherForecastMediumServiceMapper {

    const val FORECAST_LIST_SIZE = 4
    const val SECOND_ITEM_IN_LIST = 1

    operator fun invoke(timeSerieList: List<TimeSerie>, stepSize: Int): List<WeatherForecastMediumServiceModel> =
        getWeatherForecastMediumServiceModelList(
            timeSerieList = timeSerieList,
            stepSize = stepSize
        )

    private fun getWeatherForecastMediumServiceModelList(timeSerieList: List<TimeSerie>, stepSize: Int): List<WeatherForecastMediumServiceModel> {

        val list: MutableList<WeatherForecastMediumServiceModel> = mutableListOf()

        for (position in SECOND_ITEM_IN_LIST until timeSerieList.size step stepSize) {

            if (list.size >= FORECAST_LIST_SIZE) break

            val timeSerie: TimeSerie = timeSerieList[position]
            val weatherForecastMediumServiceModel: WeatherForecastMediumServiceModel =
                getWeatherForecastMediumServiceModel(timeSerie)

            list.add(weatherForecastMediumServiceModel)
        }

        return list
    }

    private fun getWeatherForecastMediumServiceModel(timeSerie: TimeSerie): WeatherForecastMediumServiceModel {

        val dateTime: String = timeSerie.validTime
        var temperature: Double = 0.0
        var windSpeed: Double = 0.0
        var weatherIcon: Int = 0

        timeSerie.parameters.forEach { parameter: Parameter ->
            when (parameter.name) {
                "ws" -> windSpeed = parameter
                    .values
                    .first()
                "t" -> temperature = parameter
                    .values
                    .first()
                "Wsymb2" -> weatherIcon = parameter
                    .values
                    .first()
                    .toInt()
            }
        }

        val weatherIconResource = WeatherForecastUtil
            .getWsymb2IconResourceId(weatherIcon)

        return WeatherForecastMediumServiceModel(
            dateTime = dateTime,
            temperature = temperature,
            windSpeed = windSpeed,
            weatherIconResource = weatherIconResource
        )
    }
}