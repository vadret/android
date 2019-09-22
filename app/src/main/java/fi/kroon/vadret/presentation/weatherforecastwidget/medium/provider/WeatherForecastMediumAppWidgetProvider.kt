package fi.kroon.vadret.presentation.weatherforecastwidget.medium.provider

import android.app.AlarmManager
import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetManager.ACTION_APPWIDGET_UPDATE
import android.appwidget.AppWidgetManager.EXTRA_APPWIDGET_IDS
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.Intent.ACTION_BOOT_COMPLETED
import android.net.Uri
import android.view.View
import android.widget.RemoteViews
import androidx.annotation.LayoutRes
import fi.kroon.vadret.BaseApplication.Companion.appComponent
import fi.kroon.vadret.R
import fi.kroon.vadret.data.exception.Failure
import fi.kroon.vadret.presentation.weatherforecastwidget.medium.provider.di.WeatherForecastMediumComponent
import fi.kroon.vadret.presentation.weatherforecastwidget.medium.provider.di.WeatherForecastMediumFeatureScope
import fi.kroon.vadret.presentation.weatherforecastwidget.medium.provider.model.WeatherForecastMediumModel
import fi.kroon.vadret.presentation.weatherforecastwidget.medium.service.WeatherForecastMediumService
import fi.kroon.vadret.presentation.weatherforecastwidget.shared.BaseAppWidgetProvider
import fi.kroon.vadret.util.DEGREE_SYMBOL
import fi.kroon.vadret.util.HUMIDITY_SUFFIX
import fi.kroon.vadret.util.MPS_SUFFIX
import fi.kroon.vadret.util.common.WeatherForecastUtil.getWsymb2IconResourceId
import fi.kroon.vadret.util.common.WeatherForecastUtil.getWsymb2ResourceId
import fi.kroon.vadret.util.extension.toObservable
import io.github.sphrak.either.Either
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.subjects.PublishSubject
import org.threeten.bp.LocalDateTime
import org.threeten.bp.format.DateTimeFormatter
import timber.log.Timber

@WeatherForecastMediumFeatureScope
class WeatherForecastMediumAppWidgetProvider : BaseAppWidgetProvider() {

    private lateinit var cmp: WeatherForecastMediumComponent

    @LayoutRes
    private var layoutId: Int = R.layout.weather_forecast_widget_medium_item

    private var injected = false

    private val viewModel: WeatherForecastMediumViewModel by lazy {
        cmp.provideWeatherForecastMediumViewModel()
    }

    private val subscriptions: CompositeDisposable by lazy {
        cmp.provideCompositeDisposable()
    }

    private val onWidgetInitialisedSubject: PublishSubject<WeatherForecastMediumView.Event.OnWidgetInitialised> by lazy {
        cmp.provideOnWidgetInitialised()
    }

    private val onWidgetUpdatedSubject: PublishSubject<WeatherForecastMediumView.Event.OnWidgetUpdated> by lazy {
        cmp.provideOnWidgetUpdated()
    }

    private val onBootCompletedSubject: PublishSubject<WeatherForecastMediumView.Event.OnBootCompleted> by lazy {
        cmp.provideOnBootCompleted()
    }
    private val context: Context by lazy {
        cmp.provideContext()
    }

    private val componentName: ComponentName by lazy {
        ComponentName(context, WeatherForecastMediumAppWidgetProvider::class.java)
    }

    private val alarmManager: AlarmManager by lazy {
        context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    }

    private val appWidgetIds: IntArray by lazy {
        appWidgetManager.getAppWidgetIds(componentName)
    }

    private val providerIntent: Intent by lazy {
        Intent(context, WeatherForecastMediumAppWidgetProvider::class.java)
    }

    private val serviceIntent: Intent by lazy {
        Intent(context, WeatherForecastMediumService::class.java)
    }

    private val dateTimeFormat: DateTimeFormatter by lazy {
        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
    }

    private val pendingIntent: PendingIntent by lazy {
        PendingIntent
            .getBroadcast(
                context,
                0,
                providerIntent,
                PendingIntent.FLAG_CANCEL_CURRENT
            )
    }

    private fun inject(context: Context?) {
        if (!injected) {
            context?.let {
                cmp = initialise(context)
                cmp.inject(this)
                injected = true
            }
        }
    }

    private fun initialise(context: Context): WeatherForecastMediumComponent =
        appComponent(context)
            .weatherForecastMediumWidgetComponentBuilder()
            .build()

    override fun onEnabled(context: Context?) {
        super.onEnabled(context)
        Timber.d("ON ENABLED")
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        super.onReceive(context, intent)
        Timber.d("ON RECEIVE: ${intent?.action}")

        intent?.let {
            inject(context)
            if (intent.action == ACTION_BOOT_COMPLETED) {
                Timber.d("ON BOOT COMPLETE RECEIVED")
                onBootComplete()
            }
        }
    }

    override fun onUpdate(context: Context?, appWidgetManager: AppWidgetManager?, appWidgetIds: IntArray?) {
        super.onUpdate(context, appWidgetManager, appWidgetIds)
        Timber.d("ON UPDATE")

        inject(context)
        setupEvents()

        context?.let {
            Timber.d("CONTEXT AVAILABLE")
            appWidgetManager?.let {
                Timber.d("APPWIDGETMANAGER AVAILABLE")
                appWidgetIds?.forEach { appWidgetId: Int ->
                    Timber.d("APPWIDGET ID: $appWidgetId")
                    onWidgetInitialisedSubject.onNext(
                        WeatherForecastMediumView
                            .Event
                            .OnWidgetInitialised(appWidgetId)
                    )
                }
            }
        }
    }

    override fun onDisabled(context: Context?) {
        super.onDisabled(context)
        Timber.d("ON DISABLED")
    }

    override fun onDeleted(context: Context?, appWidgetIds: IntArray?) {
        super.onDeleted(context, appWidgetIds)
        Timber.d("ON DELETED")
    }

    private fun setupLayout(appWidgetId: Int) = viewModel
        .getAppWidgetTheme(appWidgetId)
        .map { result: Either<Failure, Int> ->
            result.either(
                { failure: Failure ->
                    Timber.e("Failure: $failure")
                },
                { layout: Int ->
                    Timber.i("Changed layout from $layoutId to $layout")
                    layoutId = layout
                }
            )
        }.blockingGet()

    private fun onBootComplete() {
        Timber.d("ON BOOT COMPLETED IDS: ${appWidgetIds.size}")
        setupEvents()
        appWidgetIds.forEach { appWidgetId: Int ->
            Timber.d("ON BOOT COMPLETE RECEIVED FOR ID: $appWidgetId")
            onBootCompletedSubject
                .onNext(
                    WeatherForecastMediumView
                        .Event
                        .OnBootCompleted(appWidgetId)
                )
        }
    }

    private fun updateAppWidget(weather: WeatherForecastMediumModel, appWidgetId: Int) {
        Timber.d("UPDATE APPWIDGET LOCALITY NAME: ${weather.localityName}")
        Timber.d("UPDATE APPWIDGET TEMPERATURE: ${weather.temperature}")

        setupLayout(appWidgetId)

        val remoteViews = RemoteViews(context.packageName, layoutId)

        weather.localityName?.let {

            Timber.d("LOCALITY NAME: $it")
            val temperature: String = weather.temperature.toString() + DEGREE_SYMBOL
            val localityName: String = weather.localityName
            val feelsLikeTemperature = weather.feelsLikeTemperature + DEGREE_SYMBOL
            val humidity = weather.humidityPercent.toString() + HUMIDITY_SUFFIX
            val windSpeed = weather.windSpeed.toString() + MPS_SUFFIX

            serviceIntent.data = Uri.fromParts("content", appWidgetId.toString(), null)
            Timber.d("SET GRIDVIEW INTENT: $serviceIntent")

            remoteViews.setRemoteAdapter(R.id.weatherForecastMediumGridView, serviceIntent)

            appWidgetManager
                .notifyAppWidgetViewDataChanged(
                    appWidgetId,
                    R.id.weatherForecastMediumGridView
                )

            weather.windSpeed?.let {
                remoteViews.setTextViewText(
                    R.id.weatherMediumWindSpeed,
                    windSpeed
                )
                remoteViews.setViewVisibility(R.id.weatherMediumWindSpeedIcon, View.VISIBLE)
            } ?: run {
                remoteViews.setViewVisibility(R.id.weatherMediumWindSpeedIcon, View.INVISIBLE)
            }

            weather.humidityPercent?.let {
                remoteViews.setTextViewText(R.id.weatherMediumHumidity, humidity)
                remoteViews.setViewVisibility(R.id.weatherMediumHumidityIcon, View.VISIBLE)
            } ?: run {
                remoteViews.setViewVisibility(R.id.weatherMediumHumidityIcon, View.INVISIBLE)
            }

            if (weather.riskForThunder) {
                Timber.d("RISK FOR THUNDER: ${weather.riskForThunder}, ")

                val riskForThunder = context.getString(R.string.risk_for_thunder)
                remoteViews.setTextViewText(
                    R.id.weatherMediumRiskForThunder,
                    riskForThunder
                )
                remoteViews.setInt(
                    R.id.weatherMediumRiskForThunderIcon,
                    "setImageResource",
                    R.drawable.wsymb2_lightning
                )
            } else {
                remoteViews.setViewVisibility(R.id.weatherMediumRiskForThunder, View.GONE)
                remoteViews.setViewVisibility(R.id.weatherMediumRiskForThunderIcon, View.GONE)
            }

            weather.wsymb2Icon?.let {
                val wsymb2IconResource: Int = getWsymb2IconResourceId(it)

                remoteViews.setInt(
                    R.id.weatherMediumWeatherIcon,
                    "setImageResource",
                    wsymb2IconResource
                )
            }

            weather.precipitationCode?.let {
                val precipitationCode = getWsymb2ResourceId(it)
                val precipitationDescription = context.getString(precipitationCode)
                remoteViews.setTextViewText(
                    R.id.weatherMediumPrecipitationDescription,
                    precipitationDescription
                )
            }

            val datetimeStamp: LocalDateTime = LocalDateTime.now()

            remoteViews.setTextViewText(
                R.id.weatherMediumUpdatedAt,
                datetimeStamp
                    .format(dateTimeFormat)
                    .toString()
            )

            weather.feelsLikeTemperature?.let {
                remoteViews.setTextViewText(R.id.weatherMediumFeelsLikeTemperature, feelsLikeTemperature)
            } ?: remoteViews.setViewVisibility(R.id.weatherMediumFeelsLikeTemperature, View.GONE)

            remoteViews.setTextViewText(R.id.weatherMediumTemperature, temperature)
            remoteViews.setTextViewText(R.id.weatherMediumLocalityName, localityName)
        }

        appWidgetManager.updateAppWidget(appWidgetId, remoteViews)
    }

    private fun restoreAppWidget(appWidgetId: Int, updateIntervalMillis: Long) {
        Timber.d("RESTORE APPWIDGET ID: $appWidgetId, INTERVAL: $updateIntervalMillis")

        providerIntent.apply {
            action = ACTION_APPWIDGET_UPDATE
            putExtra(EXTRA_APPWIDGET_IDS, appWidgetIds)
        }

        alarmManager.setInexactRepeating(
            AlarmManager.ELAPSED_REALTIME,
            initialTriggerAtMillis,
            updateIntervalMillis,
            pendingIntent
        )
    }

    private fun render(state: WeatherForecastMediumView.State) =
        when (state.renderEvent) {
            WeatherForecastMediumView.RenderEvent.None -> Unit
            is WeatherForecastMediumView.RenderEvent.UpdateAppWidget -> updateAppWidget(
                state.renderEvent.weather,
                state.appWidgetId
            )
            is WeatherForecastMediumView.RenderEvent.RestoreAppWidget -> restoreAppWidget(
                state.renderEvent.appWidgetId,
                state.renderEvent.updateIntervalMillis
            )
        }

    private fun setupEvents() {
        if (subscriptions.size() == 0) {
            Observable.mergeArray(
                onWidgetInitialisedSubject
                    .toObservable(),
                onWidgetUpdatedSubject
                    .toObservable(),
                onBootCompletedSubject
                    .toObservable()
            ).observeOn(
                scheduler.io()
            ).compose(
                viewModel()
            ).observeOn(
                scheduler.ui()
            ).subscribe(
                ::render
            ).addTo(
                subscriptions
            )
        }
    }
}