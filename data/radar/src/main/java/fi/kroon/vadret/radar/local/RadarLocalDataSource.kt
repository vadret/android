package fi.kroon.vadret.radar.local

internal interface RadarLocalDataSource {

    suspend fun getRadar(): RadarCacheEntity?
    suspend fun setRadar(radarCacheEntity: RadarCacheEntity)
}