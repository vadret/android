package fi.kroon.vadret.radar.projection

import java.util.concurrent.ConcurrentHashMap
import kotlin.math.PI
import kotlin.math.atan
import kotlin.math.exp
import kotlin.math.floor
import kotlin.math.ln
import kotlin.math.roundToInt
import kotlin.math.tan

private const val OUTPUT_WIDTH = 720
private const val TRANSPARENT = 0
private const val OUTSIDE = -1
private const val PIXEL_CENTER = 0.5

internal class DefaultRadarReprojection : RadarReprojection {

    private val mercatorNorth: Double = mercatorY(latitude = RADAR_NORTH_LATITUDE)
    private val mercatorSouth: Double = mercatorY(latitude = RADAR_SOUTH_LATITUDE)
    private val longitudeSpan: Double = RADAR_EAST_LONGITUDE - RADAR_WEST_LONGITUDE

    val outputWidth: Int = OUTPUT_WIDTH
    val outputHeight: Int = (OUTPUT_WIDTH * (mercatorNorth - mercatorSouth) / Math.toRadians(longitudeSpan)).roundToInt()

    private val sourceIndicesBySize: ConcurrentHashMap<Long, IntArray> = ConcurrentHashMap()

    override fun reproject(
        pixels: IntArray,
        width: Int,
        height: Int
    ): ReprojectedImage {
        val sourceIndices: IntArray = sourceIndicesBySize.computeIfAbsent(sizeKey(width = width, height = height)) {
            sourceIndices(
                sourceWidth = width,
                sourceHeight = height
            )
        }
        val outputPixels = IntArray(size = sourceIndices.size) { index: Int ->
            val sourceIndex: Int = sourceIndices[index]
            if (sourceIndex == OUTSIDE) {
                TRANSPARENT
            } else {
                pixels[sourceIndex]
            }
        }
        return ReprojectedImage(
            pixels = outputPixels,
            width = outputWidth,
            height = outputHeight
        )
    }

    private fun sourceIndices(
        sourceWidth: Int,
        sourceHeight: Int
    ): IntArray {
        val metersPerColumn: Double = (RADAR_EAST_EASTING - RADAR_WEST_EASTING) / sourceWidth
        val metersPerRow: Double = (RADAR_NORTH_NORTHING - RADAR_SOUTH_NORTHING) / sourceHeight
        val latitudes = DoubleArray(size = outputHeight) { row: Int ->
            inverseMercatorY(y = mercatorNorth - (row + PIXEL_CENTER) / outputHeight * (mercatorNorth - mercatorSouth))
        }
        val longitudes = DoubleArray(size = outputWidth) { column: Int ->
            RADAR_WEST_LONGITUDE + (column + PIXEL_CENTER) / outputWidth * longitudeSpan
        }
        return IntArray(size = outputWidth * outputHeight) { index: Int ->
            val projectedPoint: ProjectedPoint = Sweref99Tm.project(
                latitude = latitudes[index / outputWidth],
                longitude = longitudes[index % outputWidth]
            )
            val sourceColumn: Int = floor((projectedPoint.easting - RADAR_WEST_EASTING) / metersPerColumn).toInt()
            val sourceRow: Int = floor((RADAR_NORTH_NORTHING - projectedPoint.northing) / metersPerRow).toInt()
            if (sourceColumn in 0 until sourceWidth && sourceRow in 0 until sourceHeight) {
                sourceRow * sourceWidth + sourceColumn
            } else {
                OUTSIDE
            }
        }
    }

    private fun sizeKey(
        width: Int,
        height: Int
    ): Long = width.toLong() shl Int.SIZE_BITS or height.toLong()

    private fun mercatorY(latitude: Double): Double =
        ln(tan(PI / 4.0 + Math.toRadians(latitude) / 2.0))

    private fun inverseMercatorY(y: Double): Double =
        Math.toDegrees(2.0 * atan(exp(y)) - PI / 2.0)
}