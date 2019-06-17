package fi.kroon.vadret.data.weatherforecastwidget.local

import android.content.Context
import fi.kroon.vadret.R
import fi.kroon.vadret.data.exception.Failure
import fi.kroon.vadret.data.functional.Either
import fi.kroon.vadret.util.extension.asRight
import fi.kroon.vadret.util.extension.asSingle
import io.reactivex.Single
import javax.inject.Inject

class WidgetThemeLocalDataSource @Inject constructor(
    private val context: Context
) {
    operator fun invoke(): Single<Either<Failure, Array<String>>> = with(context) {
        resources
            .getStringArray(R.array.weather_widget_themes)
            .asRight()
            .asSingle()
    }
}