package fi.kroon.vadret.radar.precipitation

import androidx.compose.ui.graphics.ImageBitmap
import fi.kroon.vadret.radar.extension.colored
import fi.kroon.vadret.radar.projection.PrecipitationColorScale
import fi.kroon.vadret.radar.projection.ReprojectedImage

private val precipitationPalette: IntArray by lazy { PrecipitationColorScale.palette() }

internal class RadarEchoes(
    val reprojectedImage: ReprojectedImage,
    val image: ImageBitmap
) {

    val coloredImage: ImageBitmap by lazy { reprojectedImage.colored(palette = precipitationPalette) }
}