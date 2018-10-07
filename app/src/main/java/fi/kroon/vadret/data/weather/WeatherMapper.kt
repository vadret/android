package fi.kroon.vadret.data.weather

import fi.kroon.vadret.data.weather.model.TimeSerie
import fi.kroon.vadret.utils.extensions.parseToLocalDate

class WeatherMapper {

    fun toAnyList(timeSerieList: List<TimeSerie>): List<Any> =
        if (timeSerieList.isNotEmpty()) {
            mapTimeSerieList(timeSerieList)
        } else {
            listOf()
        }

    private fun mapTimeSerieList(timeSerieList: List<TimeSerie>): MutableList<Any> {
        val newAnyList: MutableList<Any> = mutableListOf()
        var currentDate = timeSerieList.first().validTime.parseToLocalDate()
        var re: TimeSerie? = null
        for (timeSerie in timeSerieList) {
            if (!newAnyList.contains(currentDate)) {
                newAnyList.add(currentDate)
            }
            if (re != null) {
                newAnyList.add(re)
                re = null
            }
            if (timeSerie.validTime.parseToLocalDate() == currentDate) {
                newAnyList.add(timeSerie)
            } else {
                re = timeSerie
                currentDate = timeSerie.validTime.parseToLocalDate()
            }
        }
        return newAnyList
    }
}