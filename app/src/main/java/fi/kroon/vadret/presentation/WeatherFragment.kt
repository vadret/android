package fi.kroon.vadret.presentation

import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat.checkSelfPermission
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.android.material.snackbar.Snackbar
import fi.kroon.vadret.R
import fi.kroon.vadret.data.Request
import fi.kroon.vadret.data.exception.Either
import fi.kroon.vadret.data.exception.Failure
import fi.kroon.vadret.data.location.model.Location
import fi.kroon.vadret.data.location.exception.LocationFailure
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
import kotlinx.android.synthetic.main.weather_fragment.*
import javax.inject.Inject

const val TAG = "WeatherFragment"

class WeatherFragment : BaseFragment() {

    val REQUEST_ACCESS_FINE_LOCATION: Int = 1

    override fun layoutId(): Int = R.layout.weather_fragment

    @Inject
    lateinit var schedulers: Schedulers

    @Inject
    lateinit var forecastAdapter: ForecastAdapter

    private lateinit var swipeRefreshLayout: SwipeRefreshLayout

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
        rv.adapter = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initialiseView()
        requestLocationPermission()
    }

    private fun initialiseView() {
        refreshWeather.setOnRefreshListener {
            loadLocation()
            refreshWeather.isRefreshing = false
        }
        rv.layoutManager = LinearLayoutManager(this.context, RecyclerView.VERTICAL, false)
        rv.adapter = forecastAdapter
        rv.hasFixedSize()
    }

    private fun loadLocation() = locationViewModel
        .get()
        .subscribeOn(schedulers.io())
        .observeOn(schedulers.ui())
        .onErrorReturn { Either.Left(LocationFailure.LocationNotAvailableFailure()) }
        .subscribe(::locationHandler)

    private fun locationHandler(data: Either<Failure, Location>) {
        data.either(::handleFailure, ::handleLocation)
    }

    private fun handleLocation(location: Location) {
        val lat_str = "%.6f".format(location.latitude).replace(",", ".")
        val lon_str = "%.6f".format(location.longitude).replace(",", ".")
        val request = Request(
            latitude = lat_str.toDouble(),
            longitude = lon_str.toDouble()
        )
        loadWeather(request)
        refreshWeather.isRefreshing = false
    }

    private fun loadWeather(request: Request) {
        rv.toVisible()
        val disposable = weatherViewModel
                .get(request)
                .subscribeOn(schedulers.io())
                .observeOn(schedulers.ui())
                .onErrorReturn { Either.Left(Failure.IOException()) }
                .subscribe(::weatherHandler)
    }

    private fun weatherHandler(data: Either<Failure, Weather>) {
        data.either(::handleFailure, ::handleSuccess)
    }

    private fun handleFailure(failure: Failure?) {
        when (failure) {
            is WeatherFailure.NoWeatherAvailable -> renderFailure(R.string.no_weather_available)
            is Failure.IOException -> renderFailure(R.string.io_exception)
            is Failure.NetworkException -> renderFailure(R.string.network_failure)
            is LocationFailure.NoLocationPermissions -> renderFailure(R.string.no_location_permission)
            is LocationFailure.LocationNotAvailableFailure -> renderFailure(R.string.location_failure)
        }
    }

    private fun handleSuccess(weather: Weather) {
        Log.d(TAG, "HANDLING SUCCESS: $weather")
        renderSuccess(timeSerieList = weather.timeSeries)
    }

    private fun renderFailure(@StringRes message: Int) {
        return Snackbar.make(rv, message, Snackbar.LENGTH_LONG).show()
    }

    private fun renderSuccess(timeSerieList: List<TimeSerie>) {
        Log.d(TAG, "RENDERING SUCCESS: $timeSerieList")

        val mapping = WeatherMapper()

        val anylist: List<Any> = mapping.toAnyList(timeSerieList)
        Log.d(TAG, "ANY LIST: $anylist")
        forecastAdapter.collection = anylist
    }

    private fun requestLocationPermission() {
        Log.d(TAG, "Requesting permission begin")
        if (checkSelfPermission(context!!,
                        android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "Requesting: ${android.Manifest.permission.ACCESS_FINE_LOCATION}")
            Log.d(TAG, "Requesting NOW.")
            requestPermissions(
                    arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                    REQUEST_ACCESS_FINE_LOCATION
            )
        } else {
            Log.d(TAG, "Permission is already granted. Proceeding.")
            loadLocation()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when (requestCode) {
            REQUEST_ACCESS_FINE_LOCATION -> {
                // If request is cancelled, the result arrays are empty.
                Log.d(TAG, "Request Code was: $REQUEST_ACCESS_FINE_LOCATION")
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    Log.d(TAG, "Permission granted.")
                    Toast.makeText(this.context, R.string.permission_granted, Toast.LENGTH_LONG).show()
                    loadLocation()
                } else {
                    Log.d(TAG, "Permission denied.")
                    Toast.makeText(this.context, R.string.permission_missing, Toast.LENGTH_LONG).show()
                }
                return
            }
            else -> {
                Log.d(TAG, "Im leaving.")
                super.onRequestPermissionsResult(requestCode, permissions, grantResults)
            }
        }
    }
}