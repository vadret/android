package fi.kroon.vadret.presentation.weatherforecastwidget.medium.service

import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import android.widget.RemoteViewsService
import fi.kroon.vadret.BaseApplication.Companion.appComponent
import fi.kroon.vadret.R
import fi.kroon.vadret.presentation.weatherforecastwidget.medium.service.di.WeatherForecastMediumServiceComponent
import fi.kroon.vadret.presentation.weatherforecastwidget.medium.service.di.WeatherForecastMediumServiceScope
import fi.kroon.vadret.presentation.weatherforecastwidget.medium.service.model.WeatherForecastMediumServiceModel
import fi.kroon.vadret.utils.DEGREE_SYMBOL
import fi.kroon.vadret.utils.MPS_SUFFIX
import fi.kroon.vadret.utils.Schedulers
import fi.kroon.vadret.utils.extensions.toObservable
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.subjects.PublishSubject
import org.threeten.bp.OffsetDateTime
import org.threeten.bp.format.DateTimeFormatter
import org.threeten.bp.format.FormatStyle
import org.threeten.bp.format.TextStyle
import timber.log.Timber
import java.util.Locale
import javax.inject.Inject

class WeatherForecastMediumService : RemoteViewsService() {

    override fun onGetViewFactory(intent: Intent?): RemoteViewsFactory =
        WeatherForecastMediumServiceFactory(applicationContext, intent)

    @WeatherForecastMediumServiceScope
    class WeatherForecastMediumServiceFactory @Inject constructor(
        private val context: Context,
        private val intent: Intent?
    ) : RemoteViewsFactory {

        @Inject
        lateinit var viewModel: WeatherForecastMediumServiceViewModel

        @Inject
        lateinit var onInitialisedSubject: PublishSubject<WeatherForecastMediumServiceView.Event.OnInitialised>

        @Inject
        lateinit var subscriptions: CompositeDisposable

        @Inject
        lateinit var cmp: WeatherForecastMediumServiceComponent

        @Inject
        lateinit var schedulers: Schedulers

        private var injected: Boolean = false

        private val layoutId: Int = R.layout.weather_forecast_widget_medium_item

        private val list: MutableList<WeatherForecastMediumServiceModel> = mutableListOf()

        private val timeOfDayFormat: DateTimeFormatter =
            DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT)

        /**
         *  If [appWidgetId] is not derived from
         *  the intent's data property it will
         *  be wrong and all widgets
         *  will serve wrong data.
         */
        private val appWidgetId: Int by lazy {
            intent
                ?.data
                ?.schemeSpecificPart
                ?.toInt()
                ?: AppWidgetManager.INVALID_APPWIDGET_ID
        }

        override fun onCreate() {
            Timber.d("ON CREATE")
            inject(context)
            setupEvents()
        }

        override fun onDestroy() {
            Timber.d("ON DESTROY")
            list.clear()
            subscriptions.clear()
        }

        override fun onDataSetChanged() {
            Timber.d("ON DATASET CHANGED")
            onInitialisedSubject
                .onNext(
                    WeatherForecastMediumServiceView
                        .Event
                        .OnInitialised(appWidgetId)
                )
        }

        override fun getLoadingView(): RemoteViews? = null
        override fun getItemId(position: Int): Long = position.toLong()
        override fun hasStableIds(): Boolean = true

        override fun getViewAt(position: Int): RemoteViews {

            val model: WeatherForecastMediumServiceModel = list[position]
            val views = RemoteViews(context.packageName, layoutId)

            val localeDay = OffsetDateTime
                .parse(model.dateTime)
                .dayOfWeek
                .getDisplayName(
                    TextStyle.FULL_STANDALONE,
                    Locale.getDefault()
                )

            val dateTime = OffsetDateTime
                .parse(model.dateTime)
                .format(timeOfDayFormat)

            val temperature = "${model.temperature}$DEGREE_SYMBOL"
            val windSpeed = "${model.windSpeed}$MPS_SUFFIX"

            views.setTextViewText(R.id.weatherMediumLocaleDay, localeDay)
            views.setTextViewText(R.id.weatherMediumDateTime, dateTime)
            views.setTextViewText(R.id.weatherMediumTemperature, temperature)
            views.setInt(R.id.weatherMediumDayIcon, "setImageResource", model.weatherIconResource)
            views.setTextViewText(R.id.weatherMediumWindSpeed, windSpeed)

            return views
        }

        override fun getCount(): Int = list.size
        override fun getViewTypeCount(): Int = 1

        private fun inject(context: Context?) {
            if (!injected) {
                context?.let {
                    cmp = initialise(context)
                    cmp.inject(this)
                    injected = true
                }
            }
        }

        private fun initialise(context: Context): WeatherForecastMediumServiceComponent =
            appComponent(context)
                .weatherForecastMediumServiceComponentBuilder()
                .build()

        /**
         *  This must be executed synchronously
         *  otherwise by the time
         *  [list.size] is evaluated it will
         *  be zero and no further methods
         *  are invoked.
         */
        private fun setupEvents() {
            if (subscriptions.size() == 0) {
                Observable.mergeArray(
                    onInitialisedSubject
                        .toObservable()
                ).compose(
                    viewModel()
                ).observeOn(
                    schedulers.ui()
                ).subscribe(
                    ::render
                ).addTo(
                    subscriptions
                )
            }

            onInitialisedSubject
                .onNext(
                    WeatherForecastMediumServiceView
                        .Event
                        .OnInitialised(appWidgetId)
                )
        }

        private fun render(state: WeatherForecastMediumServiceView.State): Unit = when (state.renderEvent) {
            WeatherForecastMediumServiceView.RenderEvent.None -> Unit
            is WeatherForecastMediumServiceView.RenderEvent.UpdateWeatherForecastList -> updateWeatherForecastList(state.renderEvent.weatherForecastMediumServiceModelList)
        }

        private fun updateWeatherForecastList(weatherForecastMediumServiceModelList: List<WeatherForecastMediumServiceModel>) {
            Timber.d("UPDATE WEATHER FORECAST LIST: ${weatherForecastMediumServiceModelList.size}")
            list.clear()
            list.addAll(weatherForecastMediumServiceModelList)
        }
    }
}