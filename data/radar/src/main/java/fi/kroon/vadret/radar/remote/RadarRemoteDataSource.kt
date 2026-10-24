package fi.kroon.vadret.radar.remote

import kotlinx.datetime.LocalDate

internal interface RadarRemoteDataSource {

    /**
     *  SMHI publishes the radar per UTC [date].
     */
    suspend fun getRadar(date: LocalDate): RadarDto
}