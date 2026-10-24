package fi.kroon.vadret.radar.projection

import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.floor
import kotlin.math.ln
import kotlin.math.tan
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

private const val SOURCE_WIDTH = 471
private const val SOURCE_HEIGHT = 887
private const val TRANSPARENT = 0

class DefaultRadarReprojectionTest {

    private val radarReprojection = DefaultRadarReprojection()
    private val sourcePixels = IntArray(size = SOURCE_WIDTH * SOURCE_HEIGHT) { index: Int -> index + 1 }
    private val reprojectedImage: ReprojectedImage = radarReprojection.reproject(
        pixels = sourcePixels,
        width = SOURCE_WIDTH,
        height = SOURCE_HEIGHT
    )

    private fun mercatorY(latitude: Double): Double = ln(tan(PI / 4.0 + Math.toRadians(latitude) / 2.0))

    private fun outputPixel(
        latitude: Double,
        longitude: Double
    ): Int {
        val column: Int = floor((longitude - RADAR_WEST_LONGITUDE) / (RADAR_EAST_LONGITUDE - RADAR_WEST_LONGITUDE) * reprojectedImage.width).toInt()
        val row: Int = floor(
            (mercatorY(latitude = RADAR_NORTH_LATITUDE) - mercatorY(latitude = latitude)) /
                (mercatorY(latitude = RADAR_NORTH_LATITUDE) - mercatorY(latitude = RADAR_SOUTH_LATITUDE)) * reprojectedImage.height
        ).toInt()
        return reprojectedImage.pixels[row * reprojectedImage.width + column]
    }

    private fun sourcePixel(
        latitude: Double,
        longitude: Double
    ): Pair<Int, Int> {
        val projectedPoint: ProjectedPoint = Sweref99Tm.project(
            latitude = latitude,
            longitude = longitude
        )
        val column: Int = floor((projectedPoint.easting - RADAR_WEST_EASTING) / ((RADAR_EAST_EASTING - RADAR_WEST_EASTING) / SOURCE_WIDTH)).toInt()
        val row: Int = floor((RADAR_NORTH_NORTHING - projectedPoint.northing) / ((RADAR_NORTH_NORTHING - RADAR_SOUTH_NORTHING) / SOURCE_HEIGHT)).toInt()
        return column to row
    }

    @Test
    fun `the output keeps the proportions of web mercator`() {
        val expectedHeight: Double = reprojectedImage.width *
            (mercatorY(latitude = RADAR_NORTH_LATITUDE) - mercatorY(latitude = RADAR_SOUTH_LATITUDE)) /
            Math.toRadians(RADAR_EAST_LONGITUDE - RADAR_WEST_LONGITUDE)

        assertEquals(expected = 720, actual = reprojectedImage.width)
        assertTrue(actual = abs(reprojectedImage.height - expectedHeight) < 1.0)
        assertEquals(expected = reprojectedImage.width * reprojectedImage.height, actual = reprojectedImage.pixels.size)
    }

    @Test
    fun `places land on the radar pixel that covers them`() {
        val places: List<Pair<Double, Double>> = listOf(
            59.3293 to 18.0686,
            57.7089 to 11.9746,
            67.8558 to 20.2253,
            55.605 to 13.0038,
            63.8258 to 20.263
        )

        places.forEach { (latitude: Double, longitude: Double) ->
            val sourceIndex: Int = outputPixel(latitude = latitude, longitude = longitude) - 1
            val (expectedColumn: Int, expectedRow: Int) = sourcePixel(latitude = latitude, longitude = longitude)

            assertTrue(
                actual = abs(sourceIndex % SOURCE_WIDTH - expectedColumn) <= 1 && abs(sourceIndex / SOURCE_WIDTH - expectedRow) <= 1,
                message = "$latitude, $longitude came from column ${sourceIndex % SOURCE_WIDTH} row ${sourceIndex / SOURCE_WIDTH}, expected $expectedColumn, $expectedRow"
            )
        }
    }

    @Test
    fun `corners outside the radar area are transparent`() {
        val corners: List<Pair<Double, Double>> = listOf(
            RADAR_NORTH_LATITUDE - 0.01 to RADAR_EAST_LONGITUDE - 0.01,
            RADAR_SOUTH_LATITUDE + 0.01 to RADAR_WEST_LONGITUDE + 0.01,
            RADAR_SOUTH_LATITUDE + 0.01 to RADAR_EAST_LONGITUDE - 0.01
        )

        assertEquals(
            expected = List(size = corners.size) { TRANSPARENT },
            actual = corners.map { (latitude: Double, longitude: Double) -> outputPixel(latitude = latitude, longitude = longitude) }
        )
    }

    @Test
    fun `every source pixel the output shows is from inside the image`() {
        assertTrue(actual = reprojectedImage.pixels.all { pixel: Int -> pixel in 0..sourcePixels.size })
        assertTrue(actual = reprojectedImage.pixels.count { pixel: Int -> pixel != TRANSPARENT } > reprojectedImage.pixels.size / 2)
    }
}