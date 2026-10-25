package fi.kroon.vadret.radar.wind

import android.graphics.Bitmap
import fi.kroon.vadret.wind.model.WindFrame
import fi.kroon.vadret.wind.model.WindGrid
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import kotlin.concurrent.atomics.AtomicReference

private const val RASTER_WIDTH = 240

private class PreparedRaster(
    val windGrid: WindGrid,
    val windRaster: WindRaster
)

internal class DefaultWindFieldRenderer(
    private val coroutineDispatcher: CoroutineDispatcher
) : WindFieldRenderer {

    private val preparedRaster: AtomicReference<PreparedRaster?> = AtomicReference(value = null)

    override suspend fun render(
        windGrid: WindGrid,
        windFrame: WindFrame,
        windPalette: WindPalette
    ): WindField =
        withContext(context = coroutineDispatcher) {
            val windFieldValues: WindFieldValues = windRaster(windGrid = windGrid).render(
                windFrame = windFrame,
                windPalette = windPalette
            )
            WindField(
                values = windFieldValues,
                bitmap = Bitmap.createBitmap(
                    windFieldValues.colors,
                    windFieldValues.width,
                    windFieldValues.height,
                    Bitmap.Config.ARGB_8888
                )
            )
        }

    private fun windRaster(windGrid: WindGrid): WindRaster {
        val current: PreparedRaster? = preparedRaster.load()
        return if (current != null && current.windGrid == windGrid) {
            current.windRaster
        } else {
            val windRaster: WindRaster = windGrid.toWindRaster(width = RASTER_WIDTH)
            preparedRaster.store(
                newValue = PreparedRaster(
                    windGrid = windGrid,
                    windRaster = windRaster
                )
            )
            windRaster
        }
    }
}