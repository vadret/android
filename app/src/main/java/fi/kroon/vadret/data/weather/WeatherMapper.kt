package fi.kroon.vadret.data.weather

class WeatherMapper {

    /**
     * TimeSerie object, has a timestamp for a valid time interval (1 hr)
     *
     * This object is turned into a lightweight one immediatly since we
     * dont use all that data.
     */

/*    fun toPresentation(timeSeriesList: List<TimeSerie>): List<TimeSeriesUi> {
        return timeSeriesList.map {

            ts -> with (ts) {
                TimeSeriesUi(
                        validTime = validTime,
                        gust = ,
                        wsymb2 = getWsymb2(ts.parameters),
                        t =

                )
            }
        }
    }*/

/*    fun getWsymb2(parameterList: List<Parameter>): Wsymb2 {
        return parameterList.map { parameter ->
            when (parameter.name) {
                "Wsymb2" -> Wsymb2(level = parameter.level.toInt(), value = parameter.values[0].toInt())
            }
        }
    }*/
}