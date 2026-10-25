package fi.kroon.vadret.radar.projection

import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.atan
import kotlin.math.exp
import kotlin.math.floor
import kotlin.math.ln
import kotlin.math.tan
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

private const val SOURCE_WIDTH = 471
private const val SOURCE_HEIGHT = 887
private const val TRANSPARENT = 0
private const val NO_DATA: Byte = -1
private const val HEAVY_ECHO: Byte = -56
private const val COLOR_CHANNEL_MASK = 0xFF
private const val BITS_PER_CHANNEL = 8
private const val COLOR_CHANNELS = 4

private val places: List<Pair<Double, Double>> = listOf(
    59.3293 to 18.0686,
    57.7089 to 11.9746,
    67.8558 to 20.2253,
    55.605 to 13.0038,
    63.8258 to 20.263
)

class DefaultRadarReprojectionTest {

    private val radarReprojection = DefaultRadarReprojection()

    private fun mercatorY(latitude: Double): Double = ln(tan(PI / 4.0 + Math.toRadians(latitude) / 2.0))

    private fun raster(value: (column: Int, row: Int) -> Byte): RadarRaster =
        RadarRaster(
            values = ByteArray(size = SOURCE_WIDTH * SOURCE_HEIGHT) { index: Int -> value(index % SOURCE_WIDTH, index / SOURCE_WIDTH) },
            width = SOURCE_WIDTH,
            height = SOURCE_HEIGHT
        )

    private fun ReprojectedImage.pixelAt(
        latitude: Double,
        longitude: Double
    ): Int = pixels[outputRow(latitude = latitude) * width + outputColumn(longitude = longitude)]

    private fun ReprojectedImage.outputColumn(longitude: Double): Int = floor((longitude - RADAR_WEST_LONGITUDE) / (RADAR_EAST_LONGITUDE - RADAR_WEST_LONGITUDE) * width).toInt()

    private fun ReprojectedImage.outputRow(latitude: Double): Int =
        floor(
            (mercatorY(latitude = RADAR_NORTH_LATITUDE) - mercatorY(latitude = latitude)) /
                (mercatorY(latitude = RADAR_NORTH_LATITUDE) - mercatorY(latitude = RADAR_SOUTH_LATITUDE)) * height
        ).toInt()

    private fun ReprojectedImage.centerLongitude(column: Int): Double = RADAR_WEST_LONGITUDE + (column + 0.5) / width * (RADAR_EAST_LONGITUDE - RADAR_WEST_LONGITUDE)

    private fun ReprojectedImage.centerLatitude(row: Int): Double {
        val north: Double = mercatorY(latitude = RADAR_NORTH_LATITUDE)
        val south: Double = mercatorY(latitude = RADAR_SOUTH_LATITUDE)
        return Math.toDegrees(2.0 * atan(exp(north - (row + 0.5) / height * (north - south))) - PI / 2.0)
    }

    private fun sourceColumn(
        latitude: Double,
        longitude: Double
    ): Double =
        (Sweref99Tm.project(latitude = latitude, longitude = longitude).easting - RADAR_WEST_EASTING) /
            ((RADAR_EAST_EASTING - RADAR_WEST_EASTING) / SOURCE_WIDTH)

    private fun sourceRow(
        latitude: Double,
        longitude: Double
    ): Double =
        (RADAR_NORTH_NORTHING - Sweref99Tm.project(latitude = latitude, longitude = longitude).northing) /
            ((RADAR_NORTH_NORTHING - RADAR_SOUTH_NORTHING) / SOURCE_HEIGHT)

    private fun channels(argb: Int): List<Int> = List(size = COLOR_CHANNELS) { channel: Int -> (argb shr channel * BITS_PER_CHANNEL) and COLOR_CHANNEL_MASK }

    @Test
    fun `the output keeps the proportions of web mercator`() {
        val reprojectedImage: ReprojectedImage = radarReprojection.reproject(radarRaster = raster { _, _ -> 0 })
        val expectedHeight: Double = reprojectedImage.width *
            (mercatorY(latitude = RADAR_NORTH_LATITUDE) - mercatorY(latitude = RADAR_SOUTH_LATITUDE)) /
            Math.toRadians(RADAR_EAST_LONGITUDE - RADAR_WEST_LONGITUDE)

        assertEquals(expected = 720, actual = reprojectedImage.width)
        assertTrue(actual = abs(reprojectedImage.height - expectedHeight) < 1.0)
        assertEquals(expected = reprojectedImage.width * reprojectedImage.height, actual = reprojectedImage.pixels.size)
    }

    @Test
    fun `an echo is drawn over the place it was measured and nowhere else`() {
        places.forEach { (latitude: Double, longitude: Double) ->
            val column: Int = floor(sourceColumn(latitude = latitude, longitude = longitude)).toInt()
            val row: Int = floor(sourceRow(latitude = latitude, longitude = longitude)).toInt()
            val reprojectedImage: ReprojectedImage = radarReprojection.reproject(
                radarRaster = raster { sourceColumnIndex: Int, sourceRowIndex: Int ->
                    if (abs(sourceColumnIndex - column) <= 1 && abs(sourceRowIndex - row) <= 1) {
                        HEAVY_ECHO
                    } else {
                        0
                    }
                }
            )

            assertEquals(
                expected = PrecipitationColorScale.argb(reflectivity = 50f),
                actual = reprojectedImage.pixelAt(latitude = latitude, longitude = longitude),
                message = "$latitude, $longitude"
            )
            assertEquals(expected = TRANSPARENT, actual = reprojectedImage.pixelAt(latitude = latitude, longitude = longitude + 0.5))
            assertEquals(expected = TRANSPARENT, actual = reprojectedImage.pixelAt(latitude = latitude - 0.3, longitude = longitude))
        }
    }

    @Test
    fun `between radar pixels the echo blends`() {
        val reprojectedImage: ReprojectedImage = radarReprojection.reproject(
            radarRaster = raster { sourceColumnIndex: Int, _ ->
                if (sourceColumnIndex % 2 == 0) {
                    100.toByte()
                } else {
                    150.toByte()
                }
            }
        )

        places.forEach { (latitude: Double, longitude: Double) ->
            val outputColumn: Int = reprojectedImage.outputColumn(longitude = longitude)
            val outputRow: Int = reprojectedImage.outputRow(latitude = latitude)
            val sampleColumn: Double = sourceColumn(
                latitude = reprojectedImage.centerLatitude(row = outputRow),
                longitude = reprojectedImage.centerLongitude(column = outputColumn)
            ) - 0.5
            val leftColumn: Int = floor(sampleColumn).toInt()
            val leftValue: Double = if (leftColumn % 2 == 0) {
                100.0
            } else {
                150.0
            }
            val rightValue: Double = 250.0 - leftValue
            val value: Double = leftValue + (rightValue - leftValue) * (sampleColumn - leftColumn)
            val expected: List<Int> = channels(argb = PrecipitationColorScale.argb(reflectivity = (value * 0.4 - 30.0).toFloat()))
            val actual: List<Int> = channels(argb = reprojectedImage.pixels[outputRow * reprojectedImage.width + outputColumn])

            assertTrue(
                actual = expected.zip(actual).all { (expectedChannel: Int, actualChannel: Int) -> abs(expectedChannel - actualChannel) <= 1 },
                message = "$latitude, $longitude expected $expected, got $actual"
            )
        }
    }

    @Test
    fun `an even echo covers the radar area but not the corners outside it`() {
        val reprojectedImage: ReprojectedImage = radarReprojection.reproject(radarRaster = raster { _, _ -> 150.toByte() })
        val corners: List<Pair<Double, Double>> = listOf(
            RADAR_NORTH_LATITUDE - 0.01 to RADAR_EAST_LONGITUDE - 0.01,
            RADAR_SOUTH_LATITUDE + 0.01 to RADAR_WEST_LONGITUDE + 0.01,
            RADAR_SOUTH_LATITUDE + 0.01 to RADAR_EAST_LONGITUDE - 0.01
        )

        assertEquals(
            expected = List(size = corners.size) { TRANSPARENT },
            actual = corners.map { (latitude: Double, longitude: Double) -> reprojectedImage.pixelAt(latitude = latitude, longitude = longitude) }
        )
        assertTrue(actual = reprojectedImage.pixels.count { pixel: Int -> pixel == PrecipitationColorScale.argb(reflectivity = 30f) } > reprojectedImage.pixels.size / 2)
        assertTrue(actual = reprojectedImage.pixels.all { pixel: Int -> pixel == TRANSPARENT || pixel == PrecipitationColorScale.argb(reflectivity = 30f) })
    }

    @Test
    fun `places outside radar coverage are not drawn`() {
        val reprojectedImage: ReprojectedImage = radarReprojection.reproject(radarRaster = raster { _, _ -> NO_DATA })

        assertTrue(actual = reprojectedImage.pixels.all { pixel: Int -> pixel == TRANSPARENT })
    }
}