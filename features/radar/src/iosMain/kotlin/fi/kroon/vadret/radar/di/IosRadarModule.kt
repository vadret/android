package fi.kroon.vadret.radar.di

import coil3.PlatformContext
import fi.kroon.vadret.radar.precipitation.MotionPrecipitationRenderer
import fi.kroon.vadret.radar.precipitation.PrecipitationRenderer
import org.koin.core.module.Module
import org.koin.dsl.module

val iosRadarModule: Module = module {
    single<PlatformContext> { PlatformContext.INSTANCE }
    single<PrecipitationRenderer> { MotionPrecipitationRenderer(coroutineDispatcher = get()) }
}