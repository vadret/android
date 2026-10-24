package fi.kroon.vadret.radar.projection

internal interface RadarReprojection {
    fun reproject(
        pixels: IntArray,
        width: Int,
        height: Int
    ): ReprojectedImage
}