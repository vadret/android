package fi.kroon.vadret.radar.projection

import java.util.concurrent.ConcurrentHashMap
import kotlin.math.roundToInt

private const val OUTPUT_WIDTH = 720
private const val NO_ECHO: Byte = 0
private const val PIXEL_CENTER = 0.5
private const val BYTE_MASK = 0xFF
private const val NO_DATA = 255
private const val NO_ECHO_LEVEL = 0f

internal class DefaultRadarReprojection : RadarReprojection {

    private val mercatorNorth: Double = WebMercator.y(latitude = RADAR_NORTH_LATITUDE)
    private val mercatorSouth: Double = WebMercator.y(latitude = RADAR_SOUTH_LATITUDE)
    private val longitudeSpan: Double = RADAR_EAST_LONGITUDE - RADAR_WEST_LONGITUDE

    private val outputWidth: Int = OUTPUT_WIDTH
    private val outputHeight: Int = (OUTPUT_WIDTH * (mercatorNorth - mercatorSouth) / Math.toRadians(longitudeSpan)).roundToInt()

    private val samplePointsBySize: ConcurrentHashMap<Long, SamplePoints> = ConcurrentHashMap()

    override fun reproject(radarRaster: RadarRaster): ReprojectedImage {
        val samplePoints: SamplePoints = samplePointsBySize.computeIfAbsent(
            sizeKey(width = radarRaster.width, height = radarRaster.height)
        ) {
            samplePoints(
                sourceWidth = radarRaster.width,
                sourceHeight = radarRaster.height,
                latitudes = DoubleArray(size = outputHeight) { row: Int ->
                    WebMercator.latitude(y = mercatorNorth - (row + PIXEL_CENTER) / outputHeight * (mercatorNorth - mercatorSouth))
                },
                longitudes = DoubleArray(size = outputWidth) { column: Int ->
                    RADAR_WEST_LONGITUDE + (column + PIXEL_CENTER) / outputWidth * longitudeSpan
                }
            )
        }
        return ReprojectedImage(
            echoes = ByteArray(size = outputWidth * outputHeight) { index: Int ->
                reprojectedEcho(
                    radarRaster = radarRaster,
                    samplePoints = samplePoints,
                    index = index
                )
            },
            width = outputWidth,
            height = outputHeight
        )
    }

    private fun reprojectedEcho(
        radarRaster: RadarRaster,
        samplePoints: SamplePoints,
        index: Int
    ): Byte {
        if (samplePoints.isOutside(index = index)) {
            return NO_ECHO
        }
        val topLeft: Int = samplePoints.topLeftIndices[index]
        val right: Int = if (topLeft % radarRaster.width < radarRaster.width - 1) {
            1
        } else {
            0
        }
        val below: Int = if (topLeft / radarRaster.width < radarRaster.height - 1) {
            radarRaster.width
        } else {
            0
        }
        val columnFraction: Float = samplePoints.columnFractions[index]
        val top: Float = interpolate(
            from = radarRaster.echo(index = topLeft),
            to = radarRaster.echo(index = topLeft + right),
            fraction = columnFraction
        )
        val bottom: Float = interpolate(
            from = radarRaster.echo(index = topLeft + below),
            to = radarRaster.echo(index = topLeft + below + right),
            fraction = columnFraction
        )
        val value: Float = interpolate(
            from = top,
            to = bottom,
            fraction = samplePoints.rowFractions[index]
        )
        return value.roundToInt().toByte()
    }

    private fun RadarRaster.echo(index: Int): Float {
        val value: Int = values[index].toInt() and BYTE_MASK
        return if (value == NO_DATA) {
            NO_ECHO_LEVEL
        } else {
            value.toFloat()
        }
    }

    private fun interpolate(
        from: Float,
        to: Float,
        fraction: Float
    ): Float = from + (to - from) * fraction

    private fun sizeKey(
        width: Int,
        height: Int
    ): Long = width.toLong() shl Int.SIZE_BITS or height.toLong()
}