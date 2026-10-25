package fi.kroon.vadret.radar.wind

import kotlin.random.Random

internal const val TRAIL_LENGTH = 10

private const val MINIMUM_LIFESPAN = 30
private const val MAXIMUM_LIFESPAN = 80
private const val SPAWN_ATTEMPTS = 8

internal class WindParticles(
    val particleCount: Int,
    private val random: Random
) {

    private val historyX = FloatArray(size = particleCount * TRAIL_LENGTH)
    private val historyY = FloatArray(size = particleCount * TRAIL_LENGTH)
    private val ages = IntArray(size = particleCount)
    private val lifespans = IntArray(size = particleCount)
    private var head: Int = 0
    var isSeeded: Boolean = false
        private set

    fun advance(
        windFieldValues: WindFieldValues,
        visibleArea: RasterArea,
        rasterPixelsPerMeterPerSecond: Float
    ) {
        if (!isSeeded) {
            repeat(times = particleCount) { particle: Int ->
                respawn(
                    particle = particle,
                    windFieldValues = windFieldValues,
                    visibleArea = visibleArea
                )
            }
            isSeeded = true
            return
        }
        val previous: Int = head
        head = (head + 1) % TRAIL_LENGTH
        for (particle in 0 until particleCount) {
            val x: Float = historyX[slot(particle = particle, index = previous)]
            val y: Float = historyY[slot(particle = particle, index = previous)]
            val pixel: Int = windFieldValues.pixelAt(x = x, y = y)
            ages[particle] = ages[particle] + 1
            val eastward: Float = windFieldValues.valueAt(values = windFieldValues.eastward, pixel = pixel)
            val northward: Float = windFieldValues.valueAt(values = windFieldValues.northward, pixel = pixel)
            val isExpired: Boolean = ages[particle] > lifespans[particle] || eastward.isNaN() || northward.isNaN() || !visibleArea.contains(x = x, y = y)
            if (isExpired) {
                respawn(
                    particle = particle,
                    windFieldValues = windFieldValues,
                    visibleArea = visibleArea
                )
            } else {
                historyX[slot(particle = particle, index = head)] = x + eastward * rasterPixelsPerMeterPerSecond
                historyY[slot(particle = particle, index = head)] = y - northward * rasterPixelsPerMeterPerSecond
            }
        }
    }

    fun x(
        particle: Int,
        age: Int
    ): Float = historyX[slot(particle = particle, index = historyIndex(age = age))]

    fun y(
        particle: Int,
        age: Int
    ): Float = historyY[slot(particle = particle, index = historyIndex(age = age))]

    fun reset() {
        isSeeded = false
    }

    private fun respawn(
        particle: Int,
        windFieldValues: WindFieldValues,
        visibleArea: RasterArea
    ) {
        var x: Float = visibleArea.randomX(random = random)
        var y: Float = visibleArea.randomY(random = random)
        var attempt = 1
        while (attempt < SPAWN_ATTEMPTS && windFieldValues.isEmptyAt(x = x, y = y)) {
            x = visibleArea.randomX(random = random)
            y = visibleArea.randomY(random = random)
            attempt++
        }
        for (index in 0 until TRAIL_LENGTH) {
            historyX[slot(particle = particle, index = index)] = x
            historyY[slot(particle = particle, index = index)] = y
        }
        ages[particle] = 0
        lifespans[particle] = random.nextInt(from = MINIMUM_LIFESPAN, until = MAXIMUM_LIFESPAN + 1)
    }

    private fun historyIndex(age: Int): Int = Math.floorMod(head - age, TRAIL_LENGTH)

    private fun slot(
        particle: Int,
        index: Int
    ): Int = particle * TRAIL_LENGTH + index

    private fun WindFieldValues.isEmptyAt(
        x: Float,
        y: Float
    ): Boolean {
        val pixel: Int = pixelAt(x = x, y = y)
        return pixel == OUTSIDE_FIELD || speeds[pixel].isNaN()
    }
}