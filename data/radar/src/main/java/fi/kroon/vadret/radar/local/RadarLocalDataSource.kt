package fi.kroon.vadret.radar.local

internal interface RadarLocalDataSource {

    /**
     *  Null when nothing readable is cached.
     */
    suspend fun getRadar(): RadarCacheEntity?
    suspend fun setRadar(radarCacheEntity: RadarCacheEntity)
}