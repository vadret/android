package fi.kroon.vadret.presentation

import android.Manifest
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.appcompat.widget.SearchView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import fi.kroon.vadret.R
import fi.kroon.vadret.data.DEFAULT_LATITUDE
import fi.kroon.vadret.data.DEFAULT_LONGITUDE
import fi.kroon.vadret.data.exception.Either
import fi.kroon.vadret.data.exception.Failure
import fi.kroon.vadret.data.exception.flatMap
import fi.kroon.vadret.data.exception.map
import fi.kroon.vadret.data.location.exception.LocationFailure
import fi.kroon.vadret.data.location.model.Location
import fi.kroon.vadret.data.nominatim.NominatimRequest
import fi.kroon.vadret.data.nominatim.NominatimRequestReverse
import fi.kroon.vadret.data.nominatim.exception.NominatimFailure
import fi.kroon.vadret.data.nominatim.model.Nominatim
import fi.kroon.vadret.data.nominatim.model.NominatimList
import fi.kroon.vadret.data.nominatim.model.StatusBar
import fi.kroon.vadret.data.sharedpreferences.exception.SharedPreferencesFailure
import fi.kroon.vadret.data.weather.WeatherMapper
import fi.kroon.vadret.data.weather.WeatherRequest
import fi.kroon.vadret.data.weather.exception.WeatherFailure
import fi.kroon.vadret.data.weather.model.TimeSerie
import fi.kroon.vadret.data.weather.model.Weather
import fi.kroon.vadret.presentation.adapter.ForecastAdapter
import fi.kroon.vadret.presentation.adapter.SuggestionAdapter
import fi.kroon.vadret.presentation.adapter.diff.SuggestionDiffUtil
import fi.kroon.vadret.presentation.dialog.LocationDialog
import fi.kroon.vadret.presentation.viewmodel.LocationViewModel
import fi.kroon.vadret.presentation.viewmodel.NominatimViewModel
import fi.kroon.vadret.presentation.viewmodel.SharedPreferencesViewModel
import fi.kroon.vadret.presentation.viewmodel.SuggestionViewModel
import fi.kroon.vadret.presentation.viewmodel.WeatherViewModel
import fi.kroon.vadret.utils.Schedulers
import fi.kroon.vadret.utils.extensions.splitBySpaceTakeFirst
import fi.kroon.vadret.utils.extensions.toCoordinate
import fi.kroon.vadret.utils.extensions.toGone
import fi.kroon.vadret.utils.extensions.toInvisible
import fi.kroon.vadret.utils.extensions.toVisible
import fi.kroon.vadret.utils.extensions.viewModel
import fi.kroon.vadret.utils.extensions.withArguments
import io.reactivex.rxkotlin.addTo
import io.reactivex.rxkotlin.zipWith
import kotlinx.android.synthetic.main.weather_fragment.*
import permissions.dispatcher.NeedsPermission
import permissions.dispatcher.OnPermissionDenied
import permissions.dispatcher.RuntimePermissions
import timber.log.Timber
import javax.inject.Inject

@RuntimePermissions
class WeatherFragment : BaseFragment() {

    override fun layoutId(): Int = R.layout.weather_fragment

    lateinit var LOCATION_MODE_KEY: String
    lateinit var DEFAULT_LATITUDE_KEY: String
    lateinit var DEFAULT_LONGITUDE_KEY: String
    lateinit var DEFAULT_CITY_KEY: String
    lateinit var DEFAULT_PROVINCE_KEY: String

    @Inject
    lateinit var schedulers: Schedulers

    @Inject
    lateinit var forecastAdapter: ForecastAdapter

    @Inject
    lateinit var suggestionAdapter: SuggestionAdapter

    private lateinit var weatherViewModel: WeatherViewModel
    private lateinit var locationViewModel: LocationViewModel
    private lateinit var nominatimViewModel: NominatimViewModel
    private lateinit var suggestionViewModel: SuggestionViewModel
    private lateinit var sharedPreferencesViewModel: SharedPreferencesViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        cmp.inject(this)
        weatherViewModel = viewModel(viewModelFactory) {}
        locationViewModel = viewModel(viewModelFactory) {}
        nominatimViewModel = viewModel(viewModelFactory) {}
        suggestionViewModel = viewModel(viewModelFactory) {}
        sharedPreferencesViewModel = viewModel(viewModelFactory) {}

        LOCATION_MODE_KEY = getString(R.string.use_gps_by_defeault_key)
        DEFAULT_LATITUDE_KEY = getString(R.string.latitude_key)
        DEFAULT_LONGITUDE_KEY = getString(R.string.longitude_key)
        DEFAULT_CITY_KEY = getString(R.string.city_key)
        DEFAULT_PROVINCE_KEY = getString(R.string.province_key)
        initWithPermissionCheck()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        hideActionBar()
        forecastRv.adapter = null
        suggestionRecyclerView.adapter = null
    }

    override fun onPause() {
        disableSearchView()
        super.onPause()
    }

    override fun onResume() {
        super.onResume()
        showActionBar()
        hideAppName()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initialiseView()
        searchFab.setOnClickListener { _ ->
            if (!nominatimSearch.isVisible) {
                initialiseSearch()
                nominatimSearch.setOnQueryTextListener(
                    object : SearchView.OnQueryTextListener {
                        override fun onQueryTextSubmit(query: String?): Boolean {
                            Timber.d("OnSubmitClicked query: $query")
                            query?.let { loadNominatim(it) }
                            return false
                        }

                        override fun onQueryTextChange(newText: String): Boolean {
                            Timber.d("Text changed: $newText")
                            if (newText.isNotEmpty()) {
                                initialiseSuggestionView()
                            } else {
                                suggestionRecyclerView.toInvisible()
                            }
                            loadSuggestion(newText)
                            return true
                        }
                    }
                )

                suggestionAdapter.clickListener = {
                    Timber.d("Loading nominatim results: $it")
                    loadNominatim(it)
                    nominatimSearch.setQuery("", false)
                }

                nominatimSearch.setOnCloseListener(
                    object : SearchView.OnCloseListener {
                        override fun onClose(): Boolean {
                            disableSearchView()
                            return false
                        }
                    }
                )
            } else {
                nominatimSearch.toInvisible()
                suggestionRecyclerView.toInvisible()
            }
        }
    }

    @NeedsPermission(Manifest.permission.ACCESS_FINE_LOCATION)
    fun init() {
        loadLocationMode(LOCATION_MODE_KEY)
    }

    private fun disableSearchView() {
        searchFab.toVisible()
        nominatimSearch.toInvisible()
        suggestionRecyclerView.adapter = null
        suggestionRecyclerView.toInvisible()
    }

    private fun initialiseSuggestionView() {
        if (!suggestionRecyclerView.isVisible) {
            suggestionRecyclerView.toVisible()
            suggestionRecyclerView.adapter = suggestionAdapter
            suggestionRecyclerView.layoutManager = LinearLayoutManager(this.context, RecyclerView.VERTICAL, false)
            suggestionRecyclerView.addItemDecoration(DividerItemDecoration(this.context, RecyclerView.VERTICAL))
            suggestionRecyclerView.hasFixedSize()
        }
    }

    private fun searchHandler(data: Either<Failure, List<Nominatim>>) {
        data.either(::handleFailure, ::handleSearch)
    }

    private fun handleSearch(nominatimList: List<Nominatim>) {
        val dialog = LocationDialog()
            .withArguments { putParcelable("nominatim", NominatimList(nominatimList)) }
        dialog.onDialogDismissed = {
            Timber.d("Dialog was dismissed.")
            loadLocationMode(LOCATION_MODE_KEY)
        }
        dialog.show(fragmentManager!!, "none")
        collapseSearchView()
        searchFab.toVisible()
    }

    private fun initialiseSearch() {
        nominatimSearch.toVisible()
        nominatimSearch.setFocusable(true)
        nominatimSearch.setIconified(false)
        nominatimSearch.requestFocusFromTouch()
        searchFab.toInvisible()
    }

    private fun initialiseView() {
        refreshWeather.setOnRefreshListener {
            weatherViewModel.forceCacheInvalidationForNextRequest()
            loadLocationMode(LOCATION_MODE_KEY)
            refreshWeather.isRefreshing = false
        }
        forecastRv.layoutManager = LinearLayoutManager(this.context, RecyclerView.VERTICAL, false)
        forecastRv.adapter = forecastAdapter
        forecastRv.hasFixedSize()
    }

    @OnPermissionDenied(Manifest.permission.ACCESS_FINE_LOCATION)
    fun onPermissionDenied() {
        putBoolean(LOCATION_MODE_KEY, false)
        putString(DEFAULT_LATITUDE_KEY, DEFAULT_LATITUDE.toString())
        putString(DEFAULT_LONGITUDE_KEY, DEFAULT_LONGITUDE.toString())
        putString(getString(R.string.city_key), "Stockholm")
        putString(getString(R.string.province_key), "Stockholms län")
        Toast.makeText(this.context, getString(R.string.permission_location_denied), Toast.LENGTH_LONG).show()
        loadLocationMode(LOCATION_MODE_KEY)
    }

    private fun putBoolean(key: String, value: Boolean) = sharedPreferencesViewModel.putBoolean(key, value)
        .subscribeOn(schedulers.io())
        .observeOn(schedulers.ui())
        .onErrorReturn { _ -> Either.Left(SharedPreferencesFailure.UpdatingPreferencesFailure()) }
        .subscribe(::setPreferencesHandler)
        .addTo(subscriptions)

    private fun putString(key: String, value: String) = sharedPreferencesViewModel.putString(key, value)
        .subscribeOn(schedulers.io())
        .observeOn(schedulers.ui())
        .onErrorReturn { _ -> Either.Left(SharedPreferencesFailure.UpdatingPreferencesFailure()) }
        .subscribe(::setPreferencesHandler)
        .addTo(subscriptions)

    private fun setPreferencesHandler(data: Either<Failure, Unit>) {
        data.either({ error -> handleFailure(error) }, { _ -> Timber.d("Settings updated") })
    }

    private fun loadLocation() {
        locationViewModel
            .get()
            .subscribeOn(schedulers.io())
            .observeOn(schedulers.ui())
            .onErrorReturn { Either.Left(LocationFailure.LocationNotAvailableFailure()) }
            .subscribe(::locationHandler)
            .addTo(subscriptions)
    }

    private fun loadNominatim(name: String) = nominatimViewModel
        .get(NominatimRequest(name, addressDetails = 1))
        .subscribeOn(schedulers.io())
        .observeOn(schedulers.ui())
        .onErrorReturn { _ -> Either.Left(NominatimFailure.NominatimNotAvailable()) }
        .subscribe(::searchHandler)
        .addTo(subscriptions)

    private fun loadReverseNominatim(nominatimRequestReverse: NominatimRequestReverse) = nominatimViewModel
        .reverse(nominatimRequestReverse)
        .subscribeOn(schedulers.io())
        .observeOn(schedulers.ui())
        .onErrorReturn { _ -> Either.Left(NominatimFailure.NominatimNotAvailable()) }
        .subscribe(::nominatimReverseHandler)
        .addTo(subscriptions)

    private fun nominatimReverseHandler(data: Either<Failure, Nominatim>) {
        data.either(::handleFailure, ::renderStatusBarNominatim)
    }

    private fun renderStatusBarNominatim(nominatim: Nominatim) = renderActionBarNominatim(nominatim)
    private fun renderStatusBarLocal(statusBar: StatusBar) = (activity as MainActivity).renderStatusBarLocally(statusBar)

    private fun hideActionBar() = (activity as MainActivity).hideActionBar()
    private fun showActionBar() = (activity as MainActivity).showActionBar()
    private fun hideAppName() = (activity as MainActivity).hideAppName()

    private fun collapseSearchView() = nominatimSearch.toGone()

    private fun loadSuggestion(newText: String) = suggestionViewModel(newText.trim())
        .map { either ->

            either.map { state ->
                val diffResult = DiffUtil.calculateDiff(
                    SuggestionDiffUtil(
                        state.currentFilteredlist,
                        state.newFilteredList))
                state.copy(diffResult = diffResult)
            }
        }
        .subscribeOn(schedulers.io())
        .observeOn(schedulers.ui())
        .subscribe(
            { result ->
                result.either(
                    { error ->
                        Timber.e("Error: $error")
                    },
                    { state ->
                        suggestionAdapter
                            .collection
                            .clear()

                        suggestionAdapter
                            .collection
                            .addAll(state.newFilteredList)

                        suggestionRecyclerView.adapter?.run {
                            state.diffResult?.dispatchUpdatesTo(this)
                        }
                        "" // hack
                    }
                )
            },
            { error ->
                Timber.e("Unrecoverable error: $error")
            }
        ).addTo(subscriptions)

    private fun locationHandler(data: Either<Failure, Location>) {
        data.either(::handleFailure, ::handleLocation)
    }

    private fun handleLocation(location: Location) {
        loadWeather(
            WeatherRequest(
                latitude = location.latitude.toCoordinate(),
                longitude = location.longitude.toCoordinate()
            )
        )
        loadReverseNominatim(
            NominatimRequestReverse(
                latitude = location.latitude.toCoordinate(),
                longitude = location.longitude.toCoordinate()
            )
        )
        putString(DEFAULT_LATITUDE_KEY, location.latitude.toCoordinate().toString())
        putString(DEFAULT_LONGITUDE_KEY, location.longitude.toCoordinate().toString())
        refreshWeather.isRefreshing = false
    }

    private fun loadWeather(weatherRequest: WeatherRequest) {
        forecastRv.toVisible()
        Timber.d("loadWeather: $weatherRequest")
        weatherViewModel
            .get(weatherRequest)
            .subscribeOn(schedulers.io())
            .observeOn(schedulers.ui())
            .onErrorReturn { Either.Left(Failure.IOException()) }
            .subscribe(::weatherHandler)
            .addTo(subscriptions)
    }

    private fun weatherHandler(data: Either<Failure, Weather>) {
        data.either(::handleFailure, ::handleWeather)
    }

    private fun handleFailure(failure: Failure?) {
        when (failure) {
            is WeatherFailure.NoWeatherAvailable -> renderFailure(R.string.no_weather_available)
            is Failure.IOException -> renderFailure(R.string.io_exception)
            is Failure.NetworkException -> renderFailure(R.string.network_failure)
            is Failure.NetworkOfflineFailure -> renderFailure(R.string.no_network_available)
            is LocationFailure.NoLocationPermissions -> renderFailure(R.string.no_location_permission)
            is LocationFailure.LocationNotAvailableFailure -> renderFailure(R.string.location_failure)
            is NominatimFailure.NominatimNotAvailable -> renderFailure(R.string.search_failed)
            is SharedPreferencesFailure.MissingLatLonFailure -> renderFailure(R.string.weather_requirement_not_satisfied)
            is SharedPreferencesFailure.MissingNameFailure -> Timber.d("MissingNameFailure: $failure")
            is WeatherFailure.NoWeatherAvailableForThisLocation -> renderFailure(R.string.data_not_available_at_this_location)
            is Failure.HttpForbidden403 -> renderFailure(R.string.http_forbidden_403)
            is Failure.HttpBadRequest400 -> renderFailure(R.string.http_bad_request_400)
            is Failure.HttpInternalServerError500 -> renderFailure(R.string.http_internal_server_error_500)
            is Failure.HttpServiceUnavailable503 -> renderFailure(R.string.http_service_unavailable_503)
            is Failure.HttpGatewayTimeout504 -> renderFailure(R.string.http_gateway_timeout_504)
        }
    }

    private fun handleWeather(weather: Weather) {
        Timber.d("handleWeather: $weather")
        renderWeather(timeSerieList = weather.timeSeries)
    }

    private fun renderFailure(@StringRes message: Int) {
        return Snackbar.make(forecastRv, message, Snackbar.LENGTH_LONG).show()
    }

    private fun renderWeather(timeSerieList: List<TimeSerie>?) {
        Timber.d("Rendering timeSerieList: $timeSerieList")
        val mapping = WeatherMapper()

        timeSerieList?.let {
            val anylist: List<Any> = mapping.toAnyList(it)
            Timber.d("ANY LIST: $anylist")
            forecastAdapter.collection = anylist
        }
    }

    private fun getBooleanPreference(key: String) = sharedPreferencesViewModel
        .getBoolean(key)
        .subscribeOn(schedulers.io())
        .observeOn(schedulers.ui())
        .onErrorReturn { Either.Left(Failure.IOException()) }
        .subscribe(::locationModeHandler)
        .addTo(subscriptions)

    private fun getStringPreference(key: String) = sharedPreferencesViewModel
        .getString(key)

    private fun loadStoredLocation() =
        getStringPreference(getString(R.string.latitude_key))
            .zipWith(getStringPreference(getString(R.string.longitude_key)))
            .map { pairEither ->
                Timber.d("loadStoredLocation (internal): ${pairEither.first}, ${pairEither.second}")
                if (pairEither.first.isLeft || pairEither.second.isLeft) {
                    Either.Left(SharedPreferencesFailure.MissingLatLonFailure())
                }
                pairEither.first.flatMap { latitude ->
                    pairEither.second.flatMap { longitude ->
                        Either.Right(
                            WeatherRequest(
                                latitude = latitude.toCoordinate(),
                                longitude = longitude.toCoordinate()
                            )
                        )
                    }
                }
            }
            .doOnError { Timber.e("Error: $it") }
            .subscribeOn(schedulers.io())
            .observeOn(schedulers.ui())
            .onErrorReturn { Either.Left(Failure.IOException()) }
            .subscribe(::weatherRequestHandler)
            .addTo(subscriptions)

    private fun loadStatusBarLocal() = getStringPreference(getString(R.string.city_key))
        .zipWith(getStringPreference(getString(R.string.province_key)))
        .map { pairEither ->
            Timber.d("loadStatusBarLocal (internal): ${pairEither.first}, ${pairEither.second}")
            if (pairEither.first.isLeft || pairEither.second.isLeft) {
                Either.Left(SharedPreferencesFailure.MissingNameFailure())
            }
            pairEither.first.flatMap { city ->
                pairEither.second.flatMap { county ->
                    Either.Right(
                        StatusBar(
                            city,
                            county
                        )
                    )
                }
            }
        }
        .doOnError { Timber.e("Error: $it") }
        .subscribeOn(schedulers.io())
        .observeOn(schedulers.ui())
        .onErrorReturn { Either.Left(Failure.IOException()) }
        .subscribe(::statusBarLocalHandler)
        .addTo(subscriptions)

    private fun statusBarLocalHandler(data: Either<Failure, StatusBar>) {
        data.either(::handleFailure, ::handleStatusBarLocal)
    }

    private fun handleStatusBarLocal(statusBar: StatusBar) {
        renderStatusBarLocal(statusBar)
    }

    private fun weatherRequestHandler(data: Either<Failure, WeatherRequest>) {
        Timber.d("weatherRequestHandler: Left: ${data.isLeft}, Right: ${data.isRight}")
        data.either(::handleFailure, ::handleStoredLocation)
    }

    private fun handleStoredLocation(weatherRequest: WeatherRequest) {
        Timber.d("handleStoredLocation: $weatherRequest")
        loadWeather(weatherRequest)
        loadStatusBarLocal()
    }

    private fun loadLocationMode(key: String) {
        getBooleanPreference(key)
    }

    private fun locationModeHandler(data: Either<Failure, Boolean>) {
        Timber.d("locationModeHandler")
        data.either(::handleFailure, ::handleLocationMode)
    }

    private fun handleLocationMode(value: Boolean) {
        Timber.d("handleLocationMode: $value")
        when (value) {
            true -> loadLocation()
            false -> loadStoredLocation()
        }
    }

    fun renderActionBarNominatim(nominatim: Nominatim) {
        nominatim.let { _ ->
            if (nominatim.address?.city != null && nominatim.address.state != null) {
                val city = nominatim.address.city
                val state = nominatim.address.state.splitBySpaceTakeFirst()
                val banner: String by lazy { "$city, $state ${resources.getString(R.string.county)}" }
                Timber.d("Updating actionbar: $banner")

                putString(DEFAULT_CITY_KEY, city)
                putString(DEFAULT_PROVINCE_KEY, state)

                banner.let {
                    (activity as MainActivity).renderActionBarNominatim(it)
                }
            } else if (nominatim.address?.hamlet != null && nominatim.address.state != null) {
                val city = nominatim.address.hamlet
                val state = nominatim.address.state.splitBySpaceTakeFirst()
                val banner: String by lazy { "$city, $state ${resources.getString(R.string.county)}" }
                Timber.d("Updating actionbar: $banner")
                putString(DEFAULT_CITY_KEY, city)
                putString(DEFAULT_PROVINCE_KEY, state)
                banner.let {
                    (activity as MainActivity).renderActionBarNominatim(it)
                }
            } else if (nominatim.address?.village != null && nominatim.address.state != null) {
                val city = nominatim.address.village
                val state = nominatim.address.state.splitBySpaceTakeFirst()
                val banner: String by lazy { "$city, $state ${resources.getString(R.string.county)}" }
                Timber.d("Updating actionbar: $banner")
                putString(DEFAULT_CITY_KEY, city)
                putString(DEFAULT_PROVINCE_KEY, state)
                banner.let {
                    (activity as MainActivity).renderActionBarNominatim(it)
                }
            } else if (nominatim.address?.suburb != null && nominatim.address.state != null) {
                val city = nominatim.address.suburb
                val state = nominatim.address.state.splitBySpaceTakeFirst()
                val banner: String by lazy { "$city, $state" }
                Timber.d("Updating actionbar: $banner")
                putString(DEFAULT_CITY_KEY, city)
                putString(DEFAULT_PROVINCE_KEY, state)
                banner.let {
                    (activity as MainActivity).renderActionBarNominatim(it)
                }
            } else {
                Timber.d("Location Statusbar criteria was not met. Not rendering it.")
                Timber.d("Nominatim: $nominatim")
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        onRequestPermissionsResult(requestCode, grantResults)
    }
}