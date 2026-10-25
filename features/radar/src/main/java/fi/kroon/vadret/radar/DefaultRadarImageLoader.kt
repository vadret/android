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
import fi.kroon.vadret.radar.exception.TiffFormatException
import fi.kroon.vadret.radar.projection.RadarReprojection
import io.github.sphrak.either.Either
import io.github.sphrak.either.extension.asLeft
import io.github.sphrak.either.extension.asRight
import io.ktor.client.HttpClient
import kotlin.coroutines.cancellation.CancellationException

private val logger: Logger = LoggerFactory.getLogger(tag = "fi.kroon.vadret.radar")

internal class DefaultRadarImageLoader(
    private val context: Context,
    radarReprojection: RadarReprojection,
    httpClient: Lazy<HttpClient>
) : RadarImageLoader {

    private val imageLoader: ImageLoader = ImageLoader.Builder(context = context)
        .components {
            add(KtorNetworkFetcherFactory(httpClient = { httpClient.value }))
            add(TiffRadarDecoderFactory(radarReprojection = radarReprojection))
        }
        .build()

    override suspend fun load(imageUrl: String): Either<Failure, Bitmap> {
        val request: ImageRequest = ImageRequest.Builder(context = context)
            .data(data = imageUrl)
            .allowHardware(enable = false)
            .build()
        return when (val result: ImageResult = imageLoader.execute(request = request)) {
            is SuccessResult -> result.image.toBitmap().asRight()
            is ErrorResult -> result.throwable.toFailure(imageUrl = imageUrl).asLeft()
        }
    }
}

private fun Throwable.toFailure(imageUrl: String): Failure {
    val errorCode: ErrorCode = when (this) {
        is CancellationException -> throw this
        is TiffFormatException -> ErrorCode.INVALID_RESPONSE
        else -> ErrorCode.NETWORK_FAILURE
    }
    logger.warn { "$errorCode: radar image $imageUrl: $message" }
    return Failure(errorCode = errorCode)
}