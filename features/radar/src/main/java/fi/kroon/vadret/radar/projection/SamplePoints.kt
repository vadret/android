package fi.kroon.vadret.radar.projection

import kotlin.math.floor

private const val OUTSIDE = -1
private const val PIXEL_CENTER = 0.5

internal class SamplePoints(
    val topLeftIndices: IntArray,
    val columnFractions: FloatArray,
    val rowFractions: FloatArray
) {

    fun isOutside(index: Int): Boolean = topLeftIndices[index] == OUTSIDE
}

internal fun samplePoints(
    sourceWidth: Int,
    sourceHeight: Int,
    latitudes: DoubleArray,
    longitudes: DoubleArray
): SamplePoints {
    val metersPerColumn: Double = (RADAR_EAST_EASTING - RADAR_WEST_EASTING) / sourceWidth
    val metersPerRow: Double = (RADAR_NORTH_NORTHING - RADAR_SOUTH_NORTHING) / sourceHeight
    val outputWidth: Int = longitudes.size
    val pixelCount: Int = outputWidth * latitudes.size
    val topLeftIndices = IntArray(size = pixelCount) { OUTSIDE }
    val columnFractions = FloatArray(size = pixelCount)
    val rowFractions = FloatArray(size = pixelCount)
    for (index in 0 until pixelCount) {
        val projectedPoint: ProjectedPoint = Sweref99Tm.project(
            latitude = latitudes[index / outputWidth],
            longitude = longitudes[index % outputWidth]
        )
        val column: Double = (projectedPoint.easting - RADAR_WEST_EASTING) / metersPerColumn
        val row: Double = (RADAR_NORTH_NORTHING - projectedPoint.northing) / metersPerRow
        if (column >= 0.0 && column < sourceWidth && row >= 0.0 && row < sourceHeight) {
            val sampleColumn: Double = (column - PIXEL_CENTER).coerceIn(minimumValue = 0.0, maximumValue = sourceWidth - 1.0)
            val sampleRow: Double = (row - PIXEL_CENTER).coerceIn(minimumValue = 0.0, maximumValue = sourceHeight - 1.0)
            val leftColumn: Int = floor(sampleColumn).toInt()
            val topRow: Int = floor(sampleRow).toInt()
            topLeftIndices[index] = topRow * sourceWidth + leftColumn
            columnFractions[index] = (sampleColumn - leftColumn).toFloat()
            rowFractions[index] = (sampleRow - topRow).toFloat()
        }
    }
    return SamplePoints(
        topLeftIndices = topLeftIndices,
        columnFractions = columnFractions,
        rowFractions = rowFractions
    )
}