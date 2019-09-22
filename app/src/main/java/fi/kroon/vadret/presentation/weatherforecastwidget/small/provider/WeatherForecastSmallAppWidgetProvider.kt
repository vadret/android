package fi.kroon.vadret.presentation.weatherforecastwidget.small.provider

import android.app.AlarmManager
import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetManager.ACTION_APPWIDGET_UPDATE
import android.appwidget.AppWidgetManager.EXTRA_APPWIDGET_IDS
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.Intent.ACTION_BOOT_COMPLETED
import android.view.View
import android.widget.RemoteViews
import androidx.annotation.LayoutRes
import fi.kroon.vadret.BaseApplication.Companion.appComponent
import fi.kroon.vadret.R
import fi.kroon.vadret.data.exception.Failure
import fi.kroon.vadret.presentation.weatherforecastwidget.shared.BaseAppWidgetProvider
import fi.kroon.vadret.presentation.weatherforecastwidget.small.provider.di.WeatherForecastSmallComponent
import fi.kroon.vadret.presentation.weatherforecastwidget.small.provider.di.WeatherForecastSmallFeatureScope
import fi.kroon.vadret.presentation.weatherforecastwidget.small.provider.model.WeatherForecastSmallModel
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
import timber.log.Timber

@WeatherForecastSmallFeatureScope
class WeatherForecastSmallAppWidgetProvider : BaseAppWidgetProvider() {

    private lateinit var cmp: WeatherForecastSmallComponent

    @LayoutRes
    private var layoutId: Int = R.layout.weather_forecast_widget_tiny_light

    private var injected = false

    private val componentName: ComponentName by lazy {
        ComponentName(context, WeatherForecastSmallAppWidgetProvider::class.java)
    }

    private val alarmManager: AlarmManager by lazy {
        context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    }

    private val appWidgetIds: IntArray by lazy {
        appWidgetManager.getAppWidgetIds(componentName)
    }

    private val providerIntent: Intent by lazy {
        Intent(context, WeatherForecastSmallAppWidgetProvider::class.java)
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

    private val viewModel: WeatherForecastSmallViewModel by lazy {
        cmp.provideWeatherForecastSmallViewModel()
    }

    private val subscriptions: CompositeDisposable by lazy {
        cmp.provideCompositeDisposable()
    }

    private val onWidgetInitialisedSubject: PublishSubject<WeatherForecastSmallView.Event.OnWidgetInitialised> by lazy {
        cmp.provideOnWidgetInitialised()
    }

    private val onWidgetUpdatedSubject: PublishSubject<WeatherForecastSmallView.Event.OnWidgetUpdated> by lazy {
        cmp.provideOnWidgetUpdated()
    }

    private val onBootCompletedSubject: PublishSubject<WeatherForecastSmallView.Event.OnBootCompleted> by lazy {
        cmp.provideOnBootCompleted()
    }

    private val context: Context by lazy {
        cmp.provideContext()
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

    private fun initialise(context: Context): WeatherForecastSmallComponent =
        appComponent(context)
            .weatherForecastSmallWidgetComponentBuilder()
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
                        WeatherForecastSmallView
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
                    WeatherForecastSmallView
                        .Event
                        .OnBootCompleted(appWidgetId)
                )
        }
    }

    private fun updateAppWidget(weather: WeatherForecastSmallModel, appWidgetId: Int) {
        Timber.d("UPDATE APPWIDGET LOCALITY NAME: ${weather.localityName}")
        Timber.d("UPDATE APPWIDGET TEMPERATURE: ${weather.temperature}")

        setupLayout(appWidgetId)

        val remoteViews: RemoteViews = RemoteViews(context.packageName, layoutId)

        weather.localityName?.let {

            val temperature: String = weather.temperature.toString() + DEGREE_SYMBOL
            val localityName: String = weather.localityName
            val feelsLikeTemperature = weather.feelsLikeTemperature + DEGREE_SYMBOL
            val humidity = weather.humidityPercent.toString() + HUMIDITY_SUFFIX
            val windSpeed = weather.windSpeed.toString() + MPS_SUFFIX

            weather.windSpeed?.let {
                remoteViews.setTextViewText(
                    R.id.weatherSmallWindSpeed,
                    windSpeed
                )
                remoteViews.setViewVisibility(R.id.weatherSmallWindSpeedIcon, View.VISIBLE)
            }

            weather.humidityPercent?.let {
                remoteViews.setTextViewText(R.id.weatherSmallHumidity, humidity)
                remoteViews.setViewVisibility(R.id.weatherSmallHumidityIcon, View.VISIBLE)
            }

            if (weather.riskForThunder) {
                val riskForThunder = context.getString(R.string.risk_for_thunder)
                remoteViews.setTextViewText(
                    R.id.weatherSmallRiskForThunder,
                    riskForThunder
                )
            } else {
                remoteViews.setViewVisibility(R.id.weatherSmallRiskForThunder, View.GONE)
                remoteViews.setViewVisibility(R.id.weatherSmallRiskForThunderIcon, View.GONE)
            }

            weather.wsymb2Icon?.let {
                val wsymb2IconResource: Int = getWsymb2IconResourceId(it)

                remoteViews.setInt(
                    R.id.weatherSmallWeatherIcon,
                    "setImageResource",
                    wsymb2IconResource
                )
            }

            weather.precipitationCode?.let {
                val precipitationCode = getWsymb2ResourceId(it)
                val precipitationDescription = context.getString(precipitationCode)
                remoteViews.setTextViewText(
                    R.id.weatherSmallPrecipitationDescription,
                    precipitationDescription
                )
            }

            weather.feelsLikeTemperature?.let {
                remoteViews.setTextViewText(R.id.weatherSmallFeelsLikeTemperature, feelsLikeTemperature)
            } ?: remoteViews.setViewVisibility(R.id.weatherSmallFeelsLikeTemperature, View.GONE)

            remoteViews.setTextViewText(R.id.weatherSmallTemperature, temperature)
            remoteViews.setTextViewText(R.id.weatherSmallLocalityName, localityName)
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

    private fun render(state: WeatherForecastSmallView.State) =
        when (state.renderEvent) {
            WeatherForecastSmallView.RenderEvent.None -> Unit
            is WeatherForecastSmallView.RenderEvent.UpdateAppWidget -> updateAppWidget(state.renderEvent.weather, state.appWidgetId)
            is WeatherForecastSmallView.RenderEvent.RestoreAppWidget -> restoreAppWidget(
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