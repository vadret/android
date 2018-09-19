package fi.kroon.vadret.data.weather

import android.util.Log
import fi.kroon.vadret.data.weather.model.TimeSerie
import org.threeten.bp.LocalDate
import org.threeten.bp.OffsetDateTime

class Mapping {
    fun toAnyList(timeSerieList: List<TimeSerie>): List<Any> {
        /** Nasty hack */
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
            Log.d("CNV", "AFTER: ${item}")
        }
        return newAnyList
    }
}