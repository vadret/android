package fi.kroon.vadret.data.weatherforecastwidget.local

import android.content.Context
import fi.kroon.vadret.R
import fi.kroon.vadret.data.exception.Failure
import fi.kroon.vadret.util.extension.asRight
import fi.kroon.vadret.util.extension.asSingle
import io.github.sphrak.either.Either
import io.reactivex.Single
import javax.inject.Inject

class WidgetForecastFormatLocalDataSource @Inject constructor(
    private val context: Context
) {
    operator fun invoke(): Single<Either<Failure, Array<String>>> = with(context) {
        resources
            .getStringArray(R.array.weather_widget_forecast_format)
            .asRight()
            .asSingle()
    }
}