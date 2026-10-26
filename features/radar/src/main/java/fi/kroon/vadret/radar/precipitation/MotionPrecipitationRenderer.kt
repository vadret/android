package fi.kroon.vadret.radar.precipitation

import android.graphics.Bitmap
import fi.kroon.vadret.radar.extension.echoLevels
import fi.kroon.vadret.radar.extension.toBitmap
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import kotlin.time.Duration

internal class MotionPrecipitationRenderer(
    private val coroutineDispatcher: CoroutineDispatcher
) : PrecipitationRenderer {

    override suspend fun render(echoes: Bitmap): PrecipitationOverlay =
        PrecipitationOverlay.Echoes(
            echoes = echoes,
            motion = null
        )

    override suspend fun renderMotion(
        echoes: Bitmap,
        nextEchoes: Bitmap,
        duration: Duration
    ): PrecipitationOverlay {
        if (echoes.width != nextEchoes.width || echoes.height != nextEchoes.height) {
            return render(echoes = echoes)
        }
        return withContext(context = coroutineDispatcher) {
            val motionField: MotionField = EchoMotionEstimator.estimate(
                previous = echoes.echoLevels(),
                next = nextEchoes.echoLevels(),
                width = echoes.width,
                height = echoes.height
            )
            PrecipitationOverlay.Echoes(
                echoes = echoes,
                motion = PrecipitationMotion(
                    nextEchoes = nextEchoes,
                    field = motionField.toBitmap(),
                    duration = duration
                )
            )
        }
    }
}