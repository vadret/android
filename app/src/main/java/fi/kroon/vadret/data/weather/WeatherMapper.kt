package fi.kroon.vadret.data.weather

import fi.kroon.vadret.data.weather.model.TimeSerie
import org.threeten.bp.LocalDate
import org.threeten.bp.OffsetDateTime
import timber.log.Timber

class WeatherMapper {
    fun toAnyList(timeSerieList: List<TimeSerie>): List<Any> =
        if (timeSerieList.isNotEmpty()) {
            mapTimeSerieList(timeSerieList)
        } else {
            listOf()
        }

    private fun mapTimeSerieList(timeSerieList: List<TimeSerie>): MutableList<Any> {
        /**
         * todo: Nasty hack
         */
        val newAnyList: MutableList<Any> = mutableListOf()
        var currentDate: LocalDate = OffsetDateTime.parse(timeSerieList.first().validTime).toLocalDate()
        var re: TimeSerie? = null
        for (timeSerie in timeSerieList) {
            if (!newAnyList.contains(currentDate)) {
                newAnyList.add(currentDate)
            }
            if (re != null) {
                newAnyList.add(re)
                re = null
            }
            if (OffsetDateTime.parse(timeSerie.validTime).toLocalDate() == currentDate) {
                newAnyList.add(timeSerie)
            } else {
                re = timeSerie
                currentDate = OffsetDateTime.parse(timeSerie.validTime).toLocalDate()
            }
        }

        for (item in newAnyList) {
            Timber.tag("CNV").d("AFTER: $item")
        }

        return newAnyList
    }
}