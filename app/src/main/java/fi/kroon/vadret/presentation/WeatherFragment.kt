package fi.kroon.vadret.presentation

import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat.checkSelfPermission
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import fi.kroon.vadret.R
import fi.kroon.vadret.data.weather.WeatherRequest
import fi.kroon.vadret.data.exception.Either
import fi.kroon.vadret.data.exception.Failure
import fi.kroon.vadret.data.location.exception.LocationFailure
import fi.kroon.vadret.data.location.model.Location
import fi.kroon.vadret.data.weather.WeatherMapper
import fi.kroon.vadret.data.weather.exception.WeatherFailure
import fi.kroon.vadret.data.weather.model.TimeSerie
import fi.kroon.vadret.data.weather.model.Weather
import fi.kroon.vadret.presentation.adapter.ForecastAdapter
import fi.kroon.vadret.presentation.viewmodel.LocationViewModel
import fi.kroon.vadret.presentation.viewmodel.WeatherViewModel
import fi.kroon.vadret.utils.Schedulers
import fi.kroon.vadret.utils.extensions.toVisible
import fi.kroon.vadret.utils.extensions.viewModel
import io.reactivex.rxkotlin.addTo
import kotlinx.android.synthetic.main.weather_fragment.*
import timber.log.Timber
import javax.inject.Inject

class WeatherFragment : BaseFragment() {
    companion object {
        const val REQUEST_ACCESS_FINE_LOCATION: Int = 1
    }

    override fun layoutId(): Int = R.layout.weather_fragment

    @Inject
    lateinit var schedulers: Schedulers

    @Inject
    lateinit var forecastAdapter: ForecastAdapter

    private lateinit var weatherViewModel: WeatherViewModel
    private lateinit var locationViewModel: LocationViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        cmp.inject(this)
        weatherViewModel = viewModel(viewModelFactory) {}
        locationViewModel = viewModel(viewModelFactory) {}
    }

    override fun onDestroyView() {
        super.onDestroyView()
        forecastRv.adapter = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initialiseView()
        requestLocationPermission()
    }

    private fun initialiseView() {
        refreshWeather.setOnRefreshListener {
            weatherViewModel.forceCacheInvalidationForNextRequest()
            loadLocation()
            refreshWeather.isRefreshing = false
        }
        forecastRv.layoutManager = LinearLayoutManager(this.context, RecyclerView.VERTICAL, false)
        forecastRv.adapter = forecastAdapter
        forecastRv.hasFixedSize()
    }

    private fun loadLocation() = locationViewModel
        .get()
        .subscribeOn(schedulers.io())
        .observeOn(schedulers.ui())
        .onErrorReturn { Either.Left(LocationFailure.LocationNotAvailableFailure()) }
        .subscribe(::locationHandler)
        .addTo(subscriptions)

    private fun locationHandler(data: Either<Failure, Location>) {
        data.either(::handleFailure, ::handleLocation)
    }

    private fun handleLocation(location: Location) {
        val latStr = "%.6f".format(location.latitude).replace(",", ".")
        val lonStr = "%.6f".format(location.longitude).replace(",", ".")
        val request = WeatherRequest(
            latitude = latStr.toDouble(),
            longitude = lonStr.toDouble()
        )
        loadWeather(request)
        refreshWeather.isRefreshing = false
    }

    private fun loadWeather(weatherRequest: WeatherRequest) {
        forecastRv.toVisible()
        weatherViewModel
            .get(weatherRequest)
            .subscribeOn(schedulers.io())
            .observeOn(schedulers.ui())
            .onErrorReturn { Either.Left(Failure.IOException()) }
            .subscribe(::weatherHandler)
            .addTo(subscriptions)
    }

    private fun weatherHandler(data: Either<Failure, Weather>) {
        data.either(::handleFailure, ::handleSuccess)
    }

    private fun handleFailure(failure: Failure?) {
        when (failure) {
            is WeatherFailure.NoWeatherAvailable -> renderFailure(R.string.no_weather_available)
            is Failure.IOException -> renderFailure(R.string.io_exception)
            is Failure.NetworkException -> renderFailure(R.string.network_failure)
            is Failure.NetworkOfflineFailure -> renderFailure(R.string.no_network_available)
            is LocationFailure.NoLocationPermissions -> renderFailure(R.string.no_location_permission)
            is LocationFailure.LocationNotAvailableFailure -> renderFailure(R.string.location_failure)
        }
    }

    private fun handleSuccess(weather: Weather) {
        Timber.d("HANDLING SUCCESS: $weather")
        renderSuccess(timeSerieList = weather.timeSeries)
    }

    private fun renderFailure(@StringRes message: Int) {
        return Snackbar.make(forecastRv, message, Snackbar.LENGTH_LONG).show()
    }

    private fun renderSuccess(timeSerieList: List<TimeSerie>) {
        Timber.d("RENDERING SUCCESS: $timeSerieList")

        val mapping = WeatherMapper()

        val anylist: List<Any> = mapping.toAnyList(timeSerieList)
        Timber.d("ANY LIST: $anylist")
        forecastAdapter.collection = anylist
    }

    private fun requestLocationPermission() {
        Timber.d("Requesting permission begin")
        if (checkSelfPermission(context!!,
                android.Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED) {
            Timber.d("Requesting: ${android.Manifest.permission.ACCESS_FINE_LOCATION}")
            Timber.d("Requesting NOW.")
            requestPermissions(
                arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                REQUEST_ACCESS_FINE_LOCATION
            )
        } else {
            Timber.d("Permission is already granted. Proceeding.")
            loadLocation()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when (requestCode) {
            REQUEST_ACCESS_FINE_LOCATION -> {
                // If request is cancelled, the result arrays are empty.
                Timber.d("WeatherRequest Code was: $REQUEST_ACCESS_FINE_LOCATION")
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    Timber.d("Permission granted.")
                    Toast.makeText(this.context, R.string.permission_granted, Toast.LENGTH_LONG).show()
                    loadLocation()
                } else {
                    Timber.d("Permission denied.")
                    Toast.makeText(this.context, R.string.permission_missing, Toast.LENGTH_LONG).show()
                }
                return
            }
            else -> {
                Timber.d("Im leaving.")
                super.onRequestPermissionsResult(requestCode, permissions, grantResults)
            }
        }
    }
}