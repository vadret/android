package fi.kroon.vadret.wind.extension

import fi.kroon.vadret.wind.local.WindForecastEntity
import fi.kroon.vadret.wind.local.WindFrameEntity
import fi.kroon.vadret.wind.model.WindForecast
import fi.kroon.vadret.wind.model.WindFrame
import fi.kroon.vadret.wind.model.WindGrid
import kotlin.time.Instant

internal fun WindForecastEntity.toWindForecast(): WindForecast =
    WindForecast(
        referenceTime = Instant.fromEpochMilliseconds(epochMilliseconds = referenceTimeEpochMillis),
        grid = WindGrid(
            columnCount = columnCount,
            rowCount = rowCount,
            longitudes = longitudes,
            latitudes = latitudes
        ),
        frames = frames.map { windFrameEntity: WindFrameEntity -> windFrameEntity.toWindFrame() }
    )

private fun WindFrameEntity.toWindFrame(): WindFrame =
    WindFrame(
        validAt = Instant.fromEpochMilliseconds(epochMilliseconds = validAtEpochMillis),
        windSpeeds = windSpeeds,
        windFromDirections = windFromDirections
    )