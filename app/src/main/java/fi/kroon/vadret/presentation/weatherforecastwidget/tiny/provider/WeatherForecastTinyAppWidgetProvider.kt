package fi.kroon.vadret.presentation.weatherforecastwidget.tiny.provider

import android.app.AlarmManager
import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetManager.ACTION_APPWIDGET_UPDATE
import android.appwidget.AppWidgetManager.EXTRA_APPWIDGET_IDS
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.Intent.ACTION_BOOT_COMPLETED
import android.widget.RemoteViews
import androidx.annotation.LayoutRes
import fi.kroon.vadret.BaseApplication.Companion.appComponent
import fi.kroon.vadret.R
import fi.kroon.vadret.data.exception.Failure
import fi.kroon.vadret.presentation.weatherforecastwidget.shared.BaseAppWidgetProvider
import fi.kroon.vadret.presentation.weatherforecastwidget.tiny.provider.di.WeatherForecastTinyComponent
import fi.kroon.vadret.presentation.weatherforecastwidget.tiny.provider.di.WeatherForecastTinyFeatureScope
import fi.kroon.vadret.presentation.weatherforecastwidget.tiny.provider.model.WeatherForecastTinyModel
import fi.kroon.vadret.util.DEGREE_SYMBOL
import fi.kroon.vadret.util.extension.toObservable
import io.github.sphrak.either.Either
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.subjects.PublishSubject
import timber.log.Timber

@WeatherForecastTinyFeatureScope
class WeatherForecastTinyAppWidgetProvider : BaseAppWidgetProvider() {

    private lateinit var cmp: WeatherForecastTinyComponent

    @LayoutRes
    private var layoutId: Int = R.layout.weather_forecast_widget_tiny_light

    private var injected = false

    private val viewModel: WeatherForecastTinyViewModel by lazy {
        cmp.provideWeatherForecastTinyViewModel()
    }

    private val subscriptions: CompositeDisposable by lazy {
        cmp.provideCompositeDisposable()
    }

    private val onWidgetInitialisedSubject: PublishSubject<WeatherForecastTinyView.Event.OnWidgetInitialised> by lazy {
        cmp.provideOnWidgetInitialised()
    }

    private val onWidgetUpdatedSubject: PublishSubject<WeatherForecastTinyView.Event.OnWidgetUpdated> by lazy {
        cmp.provideOnWidgetUpdated()
    }

    private val onBootCompletedSubject: PublishSubject<WeatherForecastTinyView.Event.OnBootCompleted> by lazy {
        cmp.provideOnBootCompleted()
    }

    private val context: Context by lazy {
        cmp.provideContext()
    }

    private val componentName: ComponentName by lazy {
        ComponentName(context, WeatherForecastTinyAppWidgetProvider::class.java)
    }

    private val alarmManager: AlarmManager by lazy {
        context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    }

    private val appWidgetIds: IntArray by lazy {
        appWidgetManager.getAppWidgetIds(componentName)
    }

    private val providerIntent: Intent by lazy {
        Intent(context, WeatherForecastTinyAppWidgetProvider::class.java)
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

    private fun initialise(context: Context): WeatherForecastTinyComponent =
        appComponent(context)
            .weatherForecastTinyWidgetComponentBuilder()
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
                        WeatherForecastTinyView
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
                    WeatherForecastTinyView
                        .Event
                        .OnBootCompleted(appWidgetId)
                )
        }
    }

    private fun updateAppWidget(weather: WeatherForecastTinyModel, appWidgetId: Int) {
        Timber.d("UPDATE APPWIDGET LOCALITY NAME: ${weather.localityName}")
        Timber.d("UPDATE APPWIDGET TEMPERATURE: ${weather.temperature}")

        setupLayout(appWidgetId)

        weather.localityName?.let {

            val remoteViews: RemoteViews = RemoteViews(context.packageName, layoutId)
            val temperature: String = weather.temperature.toString() + DEGREE_SYMBOL
            val locality: String = weather.localityName

            remoteViews.setTextViewText(R.id.weatherTinyTemperature, temperature)
            remoteViews.setTextViewText(R.id.weatherTinyLocalityName, locality)

            appWidgetManager.updateAppWidget(appWidgetId, remoteViews)
        }
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

    private fun render(state: WeatherForecastTinyView.State) =
        when (state.renderEvent) {
            WeatherForecastTinyView.RenderEvent.None -> Unit
            is WeatherForecastTinyView.RenderEvent.UpdateAppWidget -> updateAppWidget(state.renderEvent.weather, state.appWidgetId)
            is WeatherForecastTinyView.RenderEvent.RestoreAppWidget -> restoreAppWidget(
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