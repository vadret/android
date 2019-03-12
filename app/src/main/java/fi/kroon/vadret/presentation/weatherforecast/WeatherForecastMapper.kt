package fi.kroon.vadret.presentation.weatherforecast

import fi.kroon.vadret.data.location.model.Location
import fi.kroon.vadret.data.weatherforecast.model.Parameter
import fi.kroon.vadret.data.weatherforecast.model.TimeSerie
import fi.kroon.vadret.presentation.weatherforecast.model.BaseWeatherForecastModel
import fi.kroon.vadret.presentation.weatherforecast.model.WeatherForecastDateItemModel
import fi.kroon.vadret.presentation.weatherforecast.model.WeatherForecastHeadlineModel
import fi.kroon.vadret.presentation.weatherforecast.model.WeatherForecastItemModel
import fi.kroon.vadret.presentation.weatherforecast.model.WeatherForecastSplashItemModel
import fi.kroon.vadret.utils.MPS_TO_KMPH_FACTOR
import fi.kroon.vadret.utils.WINDCHILL_FORMULA_MAXIMUM
import fi.kroon.vadret.utils.WINDCHILL_FORMULA_MINIMUM
import fi.kroon.vadret.utils.common.SunsetUtil
import fi.kroon.vadret.utils.extensions.parseToLocalDate
import fi.kroon.vadret.utils.extensions.toWindChill
import org.threeten.bp.OffsetDateTime
import org.threeten.bp.ZoneId
import java.util.Calendar

object WeatherForecastMapper {

    fun toWeatherForecastModel(timeSerieList: List<TimeSerie>, location: Location): List<BaseWeatherForecastModel> =
        when {
            timeSerieList.isNotEmpty() -> {
                val baseWeatherForecastList: List<BaseWeatherForecastModel> = mapTimeSerieList(timeSerieList, location)
                baseWeatherForecastList
            }
            else -> {
                listOf()
            }
        }

    private fun mapTimeSerieList(timeSerieList: List<TimeSerie>, location: Location): List<BaseWeatherForecastModel> {

        val baseWeatherForecastModelList: MutableList<BaseWeatherForecastModel> = mutableListOf()

        var weatherForecastHeadlineModel: WeatherForecastHeadlineModel? = null
        var weatherForecastDateItemModel: WeatherForecastDateItemModel? = null
        var weatherForecastSplashItemModel: WeatherForecastSplashItemModel? = null
        var temp: TimeSerie? = null

        for (timeSerie: TimeSerie in timeSerieList) {

            if (weatherForecastHeadlineModel == null) {
                weatherForecastHeadlineModel = WeatherForecastHeadlineModel(
                    headline = getWeatherDescription(timeSerie.parameters)
                )
                baseWeatherForecastModelList.add(weatherForecastHeadlineModel)
            }

            if (weatherForecastSplashItemModel == null) {
                weatherForecastSplashItemModel = getWeatherForecastSplashItemModel(timeSerie, location)
                baseWeatherForecastModelList.add(weatherForecastSplashItemModel)
            }

            if (weatherForecastDateItemModel == null) {
                weatherForecastDateItemModel = WeatherForecastDateItemModel(
                    timeSerieList.first().validTime.parseToLocalDate()
                )
            }

            if (!baseWeatherForecastModelList.contains(weatherForecastDateItemModel)) {
                baseWeatherForecastModelList.add(weatherForecastDateItemModel)
            }

            if (temp != null) {

                val weatherForecastItemModel: WeatherForecastItemModel = getWeatherForecastItemModel(temp)
                baseWeatherForecastModelList.add(weatherForecastItemModel)
                temp = null
            }

            if (timeSerie.validTime.parseToLocalDate() == weatherForecastDateItemModel.date) {
                val weatherForecastItemModel = getWeatherForecastItemModel(timeSerie)
                baseWeatherForecastModelList.add(weatherForecastItemModel)
            } else {
                temp = timeSerie
                weatherForecastDateItemModel = WeatherForecastDateItemModel(
                    timeSerie.validTime.parseToLocalDate()
                )
            }
        }
        return baseWeatherForecastModelList.toList()
    }

    private fun getWeatherDescription(parameters: List<Parameter>): Int? {
        var description: Int? = null
        parameters.forEach { parameter: Parameter ->
            when (parameter.name) {
                "Wsymb2" -> description = parameter.values.first().toInt()
            }
        }
        return description
    }

    private fun getWeatherForecastSplashItemModel(timeSerie: TimeSerie, location: Location): WeatherForecastSplashItemModel {

        val cal: Calendar = Calendar.getInstance()
        val result: Array<Calendar>? = SunsetUtil.getSunriseSunset(cal, location.latitude, location.longitude)

        var sunriseDateTime: OffsetDateTime? = null
        var sunsetDateTime: OffsetDateTime? = null

        result?.let { dates ->
            val sunriseDateTimeInstant: org.threeten.bp.Instant = org.threeten.bp.Instant.ofEpochMilli(dates.get(0).timeInMillis)
            val sunsetDateTimeInstant: org.threeten.bp.Instant = org.threeten.bp.Instant.ofEpochMilli(dates.get(1).timeInMillis)
            sunriseDateTime = OffsetDateTime.ofInstant(sunriseDateTimeInstant, ZoneId.systemDefault())
            sunsetDateTime = OffsetDateTime.ofInstant(sunsetDateTimeInstant, ZoneId.systemDefault())
        }

        var humidityPercent: Int = 0
        var temperature: Double = 0.0
        var windSpeed: Double = 0.0
        var windDirection: Double? = null
        var feelsLikeTemperature: String? = null
        var weatherIcon: Int = 0
        var weatherDescription: Int = 0
        var precipitationCode: Int? = 0
        var thunderRisk: Int = 0

        for (param in timeSerie.parameters) {
            when (param.name) {
                "t" -> temperature = param.values.first()
                "r" -> humidityPercent = param.values.first().toInt()
                "wd" -> windDirection = param.values.first()
                "ws" -> windSpeed = param.values.first()
                "tstm" -> thunderRisk = param.values.first().toInt()
                "pcat" -> precipitationCode = param.values.first().toInt()
                "Wsymb2" -> weatherIcon = param.values.first().toInt()
            }
        }
        weatherDescription = weatherIcon

        if (temperature < WINDCHILL_FORMULA_MAXIMUM) {
            feelsLikeTemperature = if (windSpeed * MPS_TO_KMPH_FACTOR > WINDCHILL_FORMULA_MINIMUM) temperature.toWindChill(windSpeed) else null
        }

        return WeatherForecastSplashItemModel(
            sunriseDateTime = sunriseDateTime,
            sunsetDateTime = sunsetDateTime,
            humidityPercent = humidityPercent,
            temperature = temperature,
            windSpeed = windSpeed,
            windDirection = windDirection,
            feelsLikeTemperature = feelsLikeTemperature,
            weatherIcon = weatherIcon,
            weatherDescription = weatherDescription,
            precipitationCode = precipitationCode,
            thunderRisk = thunderRisk
        )
    }

    private fun getWeatherForecastItemModel(timeSerie: TimeSerie): WeatherForecastItemModel {
        var temperature: Double = 0.0
        val time: String = OffsetDateTime.parse(timeSerie.validTime).toLocalTime().toString()
        var feelsLikeTemperature: String? = null
        var windSpeed: Double = 0.0
        val precipitationType: Int = 0
        var weatherIcon: Int = 0
        var weatherDescription: Int = 0

        for (param in timeSerie.parameters) {
            when (param.name) {
                "t" -> temperature = param.values.first()
                "ws" -> windSpeed = param.values.first()
                "Wsymb2" -> weatherIcon = param.values.first().toInt()
            }
        }

        weatherDescription = weatherIcon

        if (temperature < WINDCHILL_FORMULA_MAXIMUM) {
            feelsLikeTemperature = if (windSpeed * MPS_TO_KMPH_FACTOR > WINDCHILL_FORMULA_MINIMUM) temperature.toWindChill(windSpeed) else null
        }

        return WeatherForecastItemModel(
            temperature = temperature,
            time = time,
            feelsLikeTemperature = feelsLikeTemperature,
            precipitationType = precipitationType,
            windSpeed = windSpeed,
            weatherIcon = weatherIcon,
            weatherDescription = weatherDescription
        )
    }
}