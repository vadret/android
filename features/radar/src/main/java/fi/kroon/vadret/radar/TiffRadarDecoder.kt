package fi.kroon.vadret.radar

import coil3.asImage
import coil3.decode.DecodeResult
import coil3.decode.Decoder
import coil3.decode.ImageSource
import fi.kroon.vadret.radar.extension.toEchoBitmap
import fi.kroon.vadret.radar.projection.RadarReprojection
import fi.kroon.vadret.radar.projection.ReprojectedImage
import fi.kroon.vadret.radar.tiff.TiffReader

internal class TiffRadarDecoder(
    private val imageSource: ImageSource,
    private val radarReprojection: RadarReprojection
) : Decoder {

    override suspend fun decode(): DecodeResult {
        val reprojectedImage: ReprojectedImage = radarReprojection.reproject(
            radarRaster = TiffReader.read(bytes = imageSource.source().readByteArray())
        )
        return DecodeResult(
            image = reprojectedImage.toEchoBitmap().asImage(),
            isSampled = false
        )
    }
}