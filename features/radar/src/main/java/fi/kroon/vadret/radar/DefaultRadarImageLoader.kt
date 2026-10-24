package fi.kroon.vadret.radar

import android.content.Context
import android.graphics.Bitmap
import coil3.ImageLoader
import coil3.network.ktor3.KtorNetworkFetcherFactory
import coil3.request.ErrorResult
import coil3.request.ImageRequest
import coil3.request.ImageResult
import coil3.request.SuccessResult
import coil3.request.allowHardware
import coil3.toBitmap
import fi.kroon.vadret.common.ErrorCode
import fi.kroon.vadret.common.Failure
import fi.kroon.vadret.logging.Logger
import fi.kroon.vadret.logging.LoggerFactory
import fi.kroon.vadret.radar.projection.RadarReprojection
import fi.kroon.vadret.radar.projection.ReprojectedImage
import io.github.sphrak.either.Either
import io.github.sphrak.either.extension.asLeft
import io.github.sphrak.either.extension.asRight
import io.ktor.client.HttpClient

private val logger: Logger = LoggerFactory.getLogger(tag = "fi.kroon.vadret.radar")

internal class DefaultRadarImageLoader(
    private val context: Context,
    private val radarReprojection: RadarReprojection,
    httpClient: Lazy<HttpClient>
) : RadarImageLoader {

    private val imageLoader: ImageLoader = ImageLoader.Builder(context = context)
        .components {
            add(KtorNetworkFetcherFactory(httpClient = { httpClient.value }))
        }
        .build()

    override suspend fun load(imageUrl: String): Either<Failure, Bitmap> {
        val request: ImageRequest = ImageRequest.Builder(context = context)
            .data(data = imageUrl)
            .allowHardware(enable = false)
            .build()
        return when (val result: ImageResult = imageLoader.execute(request = request)) {
            is SuccessResult -> result.image.toBitmap().toMercator().asRight()
            is ErrorResult -> {
                logger.warn { "${ErrorCode.NETWORK_FAILURE}: radar image $imageUrl: ${result.throwable.message}" }
                Failure(errorCode = ErrorCode.NETWORK_FAILURE).asLeft()
            }
        }
    }

    private fun Bitmap.toMercator(): Bitmap {
        val pixels = IntArray(size = width * height)
        getPixels(pixels, 0, width, 0, 0, width, height)
        val reprojectedImage: ReprojectedImage = radarReprojection.reproject(
            pixels = pixels,
            width = width,
            height = height
        )
        return Bitmap.createBitmap(
            reprojectedImage.pixels,
            reprojectedImage.width,
            reprojectedImage.height,
            Bitmap.Config.ARGB_8888
        )
    }
}