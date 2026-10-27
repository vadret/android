package fi.kroon.vadret.radar

import fi.kroon.vadret.common.Failure
import fi.kroon.vadret.radar.precipitation.RadarEchoes
import io.github.sphrak.either.Either

internal interface RadarImageLoader {

    suspend fun load(imageUrl: String): Either<Failure, RadarEchoes>
}