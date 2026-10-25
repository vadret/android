package fi.kroon.vadret.radar.projection

import kotlin.math.PI
import kotlin.math.atan
import kotlin.math.exp
import kotlin.math.ln
import kotlin.math.tan

internal object WebMercator {

    fun y(latitude: Double): Double = ln(tan(PI / 4.0 + Math.toRadians(latitude) / 2.0))

    fun latitude(y: Double): Double = Math.toDegrees(2.0 * atan(exp(y)) - PI / 2.0)
}