package fi.kroon.vadret.radar.precipitation

import android.graphics.Bitmap
import fi.kroon.vadret.radar.extension.colored
import fi.kroon.vadret.radar.projection.PrecipitationColorScale
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import java.util.WeakHashMap
import kotlin.time.Duration

internal class ColoredPrecipitationRenderer(
    private val coroutineDispatcher: CoroutineDispatcher
) : PrecipitationRenderer {

    private val palette: IntArray = PrecipitationColorScale.palette()
    private val lock: Any = Any()
    private val coloredByEchoes: WeakHashMap<Bitmap, Bitmap> = WeakHashMap()

    override suspend fun render(echoes: Bitmap): PrecipitationOverlay =
        withContext(context = coroutineDispatcher) {
            PrecipitationOverlay.Colored(bitmap = colored(echoes = echoes))
        }

    override suspend fun renderMotion(
        echoes: Bitmap,
        nextEchoes: Bitmap,
        duration: Duration
    ): PrecipitationOverlay = render(echoes = echoes)

    private fun colored(echoes: Bitmap): Bitmap =
        synchronized(lock) {
            coloredByEchoes.getOrPut(echoes) { echoes.colored(palette = palette) }
        }
}