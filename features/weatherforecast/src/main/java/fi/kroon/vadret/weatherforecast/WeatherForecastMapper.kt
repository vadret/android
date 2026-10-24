package fi.kroon.vadret.weatherforecast

import fi.kroon.vadret.data.location.model.Location
import fi.kroon.vadret.data.weatherforecast.model.TimeSerie
import fi.kroon.vadret.data.weatherforecast.model.WeatherData
import fi.kroon.vadret.util.common.SunsetUtil
import fi.kroon.vadret.util.common.WindChill
import fi.kroon.vadret.util.extension.parseToLocalDate
import fi.kroon.vadret.weatherforecast.model.IWeatherForecastModel
import fi.kroon.vadret.weatherforecast.model.WeatherForecastDateItemModel
import fi.kroon.vadret.weatherforecast.model.WeatherForecastHeadlineModel
import fi.kroon.vadret.weatherforecast.model.WeatherForecastItemModel
import fi.kroon.vadret.weatherforecast.model.WeatherForecastSplashItemModel
import org.threeten.bp.Instant
import org.threeten.bp.OffsetDateTime
import org.threeten.bp.ZoneId
import java.util.Calendar

object WeatherForecastMapper {

    operator fun invoke(timeSerieList: List<TimeSerie>, location: Location): List<IWeatherForecastModel> =
        when {
            timeSerieList.isNotEmpty() -> {
                val iWeatherForecastList: List<IWeatherForecastModel> = mapTimeSerieList(timeSerieList, location)
                iWeatherForecastList
            }
            else -> {
                listOf()
            }
        }

    private fun mapTimeSerieList(timeSerieList: List<TimeSerie>, location: Location): List<IWeatherForecastModel> {

        val weatherForecastModelList: MutableList<IWeatherForecastModel> = mutableListOf()

        var weatherForecastHeadlineModel: WeatherForecastHeadlineModel? = null
        var weatherForecastDateItemModel: WeatherForecastDateItemModel? = null
        var weatherForecastSplashItemModel: WeatherForecastSplashItemModel? = null
        var temp: TimeSerie? = null

        for (timeSerie: TimeSerie in timeSerieList) {

            if (weatherForecastHeadlineModel == null) {
                weatherForecastHeadlineModel = getWeatherForecastHeadlineModel(timeSerie.data)
                weatherForecastModelList.add(weatherForecastHeadlineModel)
            }

            if (weatherForecastSplashItemModel == null) {
                weatherForecastSplashItemModel = getWeatherForecastSplashItemModel(timeSerie, location)
                weatherForecastModelList.add(weatherForecastSplashItemModel)
            }

            if (weatherForecastDateItemModel == null) {
                weatherForecastDateItemModel = WeatherForecastDateItemModel(
                    timeSerieList.first().time.parseToLocalDate()
                )
            }

            if (!weatherForecastModelList.contains(weatherForecastDateItemModel)) {
                weatherForecastModelList.add(weatherForecastDateItemModel)
            }

            if (temp != null) {

                val weatherForecastItemModel: WeatherForecastItemModel = getWeatherForecastItemModel(temp)
                weatherForecastModelList.add(weatherForecastItemModel)
                temp = null
            }

            if (timeSerie.time.parseToLocalDate() == weatherForecastDateItemModel.date) {
                val weatherForecastItemModel = getWeatherForecastItemModel(timeSerie)
                weatherForecastModelList.add(weatherForecastItemModel)
            } else {
                temp = timeSerie
                weatherForecastDateItemModel = WeatherForecastDateItemModel(
                    timeSerie.time.parseToLocalDate()
                )
            }
        }
        return weatherForecastModelList.toList()
    }

    private fun getWeatherForecastHeadlineModel(data: WeatherData): WeatherForecastHeadlineModel =
        WeatherForecastHeadlineModel(
            headline = data.symbolCode,
            windSpeed = data.windSpeed,
            windDirection = data.windFromDirection
        )

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

        val data: WeatherData = timeSerie.data
        val temperature: Double = data.airTemperature
        val windSpeed: Double = data.windSpeed
        val weatherIcon: Int = data.symbolCode

        return WeatherForecastSplashItemModel(
            sunriseDateTime = sunriseDateTime,
            sunsetDateTime = sunsetDateTime,
            humidityPercent = data.relativeHumidity,
            temperature = temperature,
            windSpeed = windSpeed,
            windDirection = data.windFromDirection,
            feelsLikeTemperature = WindChill.calculate(temperature, windSpeed),
            weatherIcon = weatherIcon,
            weatherDescription = weatherIcon,
            precipitationCode = data.precipitationType,
            thunderRisk = data.thunderstormProbability
        )
    }

    private fun getWeatherForecastItemModel(timeSerie: TimeSerie): WeatherForecastItemModel {
        val data: WeatherData = timeSerie.data
        val time: String = Instant.parse(timeSerie.time).atZone(ZoneId.systemDefault()).toLocalTime().toString()
        val temperature: Double = data.airTemperature
        val windSpeed: Double = data.windSpeed
        val weatherIcon: Int = data.symbolCode
        val precipitationType: Int = 0

        return WeatherForecastItemModel(
            temperature = temperature,
            time = time,
            feelsLikeTemperature = WindChill.calculate(temperature, windSpeed),
            precipitationType = precipitationType,
            windSpeed = windSpeed,
            weatherIcon = weatherIcon,
            weatherDescription = weatherIcon
        )
    }
}