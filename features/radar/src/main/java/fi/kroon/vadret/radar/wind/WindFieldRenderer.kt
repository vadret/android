package fi.kroon.vadret.radar.wind

import fi.kroon.vadret.wind.model.WindFrame
import fi.kroon.vadret.wind.model.WindGrid

internal interface WindFieldRenderer {

    suspend fun render(
        windGrid: WindGrid,
        windFrame: WindFrame,
        windPalette: WindPalette
    ): WindField
}