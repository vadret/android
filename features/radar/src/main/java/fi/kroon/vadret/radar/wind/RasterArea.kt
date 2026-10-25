package fi.kroon.vadret.radar.wind

import kotlin.random.Random

internal data class RasterArea(
    val left: Float,
    val top: Float,
    val right: Float,
    val bottom: Float
) {

    val isEmpty: Boolean = right <= left || bottom <= top

    fun contains(
        x: Float,
        y: Float
    ): Boolean = x >= left && x < right && y >= top && y < bottom

    fun randomX(random: Random): Float = left + random.nextFloat() * (right - left)

    fun randomY(random: Random): Float = top + random.nextFloat() * (bottom - top)
}