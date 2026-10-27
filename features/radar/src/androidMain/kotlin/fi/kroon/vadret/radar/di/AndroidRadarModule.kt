package fi.kroon.vadret.radar.di

import android.os.Build
import fi.kroon.vadret.radar.precipitation.ColoredPrecipitationRenderer
import fi.kroon.vadret.radar.precipitation.MotionPrecipitationRenderer
import fi.kroon.vadret.radar.precipitation.PrecipitationRenderer
import org.koin.core.module.Module
import org.koin.dsl.module

val androidRadarModule: Module = module {
    single<PrecipitationRenderer> {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            MotionPrecipitationRenderer(coroutineDispatcher = get())
        } else {
            ColoredPrecipitationRenderer(coroutineDispatcher = get())
        }
    }
}