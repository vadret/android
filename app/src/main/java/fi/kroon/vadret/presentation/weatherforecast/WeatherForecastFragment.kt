package fi.kroon.vadret.presentation.weatherforecast

import android.Manifest
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import fi.kroon.vadret.R
import fi.kroon.vadret.data.nominatim.model.Locality
import fi.kroon.vadret.databinding.WeatherForecastFragmentBinding
import fi.kroon.vadret.presentation.main.MainActivity
import fi.kroon.vadret.presentation.weatherforecast.autocomplete.AutoCompleteAdapter
import fi.kroon.vadret.presentation.weatherforecast.autocomplete.AutoCompleteAdapterCallback
import fi.kroon.vadret.presentation.weatherforecast.di.DaggerWeatherForecastComponent
import fi.kroon.vadret.presentation.weatherforecast.di.WeatherForecastComponent
import fi.kroon.vadret.util.extension.coreComponent
import fi.kroon.vadret.util.extension.lazyAndroid
import fi.kroon.vadret.util.extension.toGone
import fi.kroon.vadret.util.extension.toInvisible
import fi.kroon.vadret.util.extension.toVisible
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import permissions.dispatcher.NeedsPermission
import permissions.dispatcher.OnNeverAskAgain
import permissions.dispatcher.OnPermissionDenied
import permissions.dispatcher.RuntimePermissions
import ru.ldralighieri.corbind.appcompat.queryTextChangeEvents
import ru.ldralighieri.corbind.swiperefreshlayout.refreshes
import ru.ldralighieri.corbind.view.clicks
import timber.log.Timber

@RuntimePermissions
class WeatherForecastFragment : Fragment() {

    private companion object {
        const val STATE_PARCEL_KEY: String = "STATE_PARCEL_KEY"
        const val SCROLL_POSITION_KEY: String = "SCROLL_POSITION_KEY"
    }

    private var isConfigChangeOrProcessDeath = false
    private var stateParcel: WeatherForecastView.StateParcel? = null
    private var bundle: Bundle? = null
    private var recyclerViewParcelable: Parcelable? = null
    private var _binding: WeatherForecastFragmentBinding? = null
    private val binding: WeatherForecastFragmentBinding get() = _binding!!

    private val component: WeatherForecastComponent by lazyAndroid {
        DaggerWeatherForecastComponent
            .factory()
            .create(context = requireContext(), coreComponent = coreComponent)
    }

    private val viewModel: WeatherForecastViewModel by lazy {
        component.provideWeatherForecastViewModel()
    }

    private val weatherForecastAdapter: WeatherForecastAdapter by lazy {
        component.provideWeatherForecastAdapter()
    }

    private val autoCompleteAdapter: AutoCompleteAdapter by lazy {
        AutoCompleteAdapter(
            callback = object : AutoCompleteAdapterCallback {
                override fun onAutoCompleteItemClicked(event: WeatherForecastView.Event.OnAutoCompleteItemClicked) {
                    viewModel.send(event = event)
                }
            }
        )
    }

    private val itemDecoration: DividerItemDecoration by lazyAndroid {
        DividerItemDecoration(requireContext(), RecyclerView.VERTICAL)
    }

    private val drawable: Drawable? by lazyAndroid {
        ContextCompat.getDrawable(requireContext(), R.drawable.search_item_divider)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Timber.d("ON CREATE -- WEATHER FORECAST")
        savedInstanceState?.let { _bundle: Bundle ->
            if (bundle == null) {
                Timber.d("savedInstanceState restored: $_bundle")
                bundle = _bundle
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = WeatherForecastFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Timber.d("ON VIEW CREATED -- WEATHER FORECAST")

        setupRecyclerView()

        viewModel
            .viewState
            .onEach(::render)
            .launchIn(viewLifecycleOwner.lifecycleScope)

        setupListeners()

        viewModel.send(
            WeatherForecastView
                .Event
                .OnViewInitialised(
                    stateParcel = bundle?.getParcelable(
                        STATE_PARCEL_KEY
                    )
                )
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        onRequestPermissionsResult(requestCode, grantResults)
    }

    override fun onStop() {
        super.onStop()
        Timber.d("ON STOP -- WEATHER FORECAST")
        isConfigChangeOrProcessDeath = true
    }

    override fun onDestroyView() {
        super.onDestroyView()
        Timber.d("ON DESTROY VIEW -- WEATHER FORECAST")

        recyclerViewParcelable =
            (binding.weatherForecastRecyclerView.layoutManager as LinearLayoutManager)
                .onSaveInstanceState()

        binding.apply {
            weatherForecastRecyclerView.adapter = null
            autoCompleteRecyclerView.adapter = null
            weatherForecastSearchView.setOnQueryTextListener(null)
        }

        hideActionBarLocalityName()

        _binding = null
    }

    override fun onDestroy() {
        super.onDestroy()
        Timber.d("ON DESTROY -- WEATHER FORECAST")
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        Timber.d("ON SAVE INSTANCE STATE -- WEATHER FORECAST")
        outState.apply {
            putParcelable(STATE_PARCEL_KEY, stateParcel)

            /**
             * If recyclerViewParcelable is available (as in not null) it gets saved
             * into the bundle.
             *
             * If user navigates away from the application the recyclerViewParcelable will
             * be null, and instead we save the scroll position via .onSavedInstanceState()
             *
             */
            recyclerViewParcelable?.run {
                putParcelable(SCROLL_POSITION_KEY, this)
            } ?: _binding?.weatherForecastRecyclerView?.layoutManager?.run {
                putParcelable(
                    SCROLL_POSITION_KEY,
                    (this as LinearLayoutManager)
                        .onSaveInstanceState()
                )
            }
        }
    }

    override fun onResume() {
        super.onResume()
        Timber.d("ON RESUME -- WEATHER FORECAST")
        if (isConfigChangeOrProcessDeath) {
            setupListeners()
            isConfigChangeOrProcessDeath = false
        }
    }

    private fun setupRecyclerView() {
        binding.weatherForecastRecyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = weatherForecastAdapter
            hasFixedSize()
        }
    }

    private fun setupListeners() {

        binding.weatherForecastSearchView
            .setOnCloseListener {

                viewModel.send(
                    WeatherForecastView
                        .Event
                        .OnSearchViewDismissed
                )

                true
            }

        binding.weatherForecastLocationSearchButton
            .clicks()
            .map {
                viewModel
                    .send(
                        WeatherForecastView.Event.OnSearchButtonToggled
                    )
            }.launchIn(viewLifecycleOwner.lifecycleScope)

        binding.weatherForecastRefresh
            .refreshes()
            .map {
                viewModel.send(WeatherForecastView.Event.OnSwipedToRefresh)
            }.launchIn(viewLifecycleOwner.lifecycleScope)

        binding.weatherForecastSearchView
            .queryTextChangeEvents()
            .debounce(200)
            .drop(1)
            .map { searchEvent ->
                when {
                    searchEvent.isSubmitted -> {
                        viewModel.send(
                            WeatherForecastView
                                .Event
                                .OnSearchButtonSubmitted(searchEvent.queryText.toString())
                        )
                    }
                    else -> {
                        viewModel.send(
                            WeatherForecastView
                                .Event
                                .OnSearchTextChanged(
                                    searchEvent.queryText.toString()
                                )
                        )
                    }
                }
            }.launchIn(viewLifecycleOwner.lifecycleScope)
    }

    private fun render(viewState: WeatherForecastView.State) =
        when (viewState.renderEvent) {
            WeatherForecastView.RenderEvent.Idle -> Unit
            WeatherForecastView.RenderEvent.RequestLocationPermission -> onRequestLocationPermission()
            WeatherForecastView.RenderEvent.StartProgressBarEffect -> startProgressBarEffect()
            WeatherForecastView.RenderEvent.StopProgressBarEffect -> stopProgressBarEffect()
            WeatherForecastView.RenderEvent.EnableSearchView -> enableSearchView()
            WeatherForecastView.RenderEvent.RestoreScrollPosition -> restoreScrollPosition()
            is WeatherForecastView.RenderEvent.DisableSearchView -> disableSearchView(viewState.renderEvent)
            is WeatherForecastView.RenderEvent.DisplayAutoComplete -> displayAutoCompleteList(
                viewState.renderEvent
            )
            is WeatherForecastView.RenderEvent.DisplayWeatherForecast -> displayWeatherForecast(
                viewState.renderEvent
            )
            is WeatherForecastView.RenderEvent.DisplayError -> renderError(viewState.renderEvent.errorCode)
            WeatherForecastView.RenderEvent.UpdateStateParcel -> updateStateParcel(viewState)
        }

    private fun startProgressBarEffect() {
        Timber.d("startProgressBarEffect")

        binding.weatherForecastLoadingProgressBar.toVisible()

        viewModel.send(
            WeatherForecastView
                .Event
                .OnProgressBarEffectStarted
        )
    }

    private fun stopProgressBarEffect() {
        Timber.d("stopProgressBarEffect")

        binding.weatherForecastLoadingProgressBar.toGone()
        binding.weatherForecastRefresh.apply {
            isRefreshing = false
        }

        viewModel.send(
            WeatherForecastView
                .Event
                .OnProgressBarEffectStopped
        )
    }

    private fun displayAutoCompleteList(renderEvent: WeatherForecastView.RenderEvent.DisplayAutoComplete) {
        autoCompleteAdapter.updateList(renderEvent.newFilteredList)
        binding.autoCompleteRecyclerView.adapter?.run {
            renderEvent.diffResult?.dispatchUpdatesTo(this)
        }
    }

    private fun restoreScrollPosition() {
        Timber.d("restoreScrollPosition")
        bundle?.run {
            (binding.weatherForecastRecyclerView.layoutManager as LinearLayoutManager)
                .onRestoreInstanceState(
                    getParcelable(SCROLL_POSITION_KEY)
                )
        }

        viewModel.send(
            WeatherForecastView
                .Event
                .OnScrollPositionRestored
        )
    }

    private fun updateStateParcel(state: WeatherForecastView.State) {
        stateParcel = WeatherForecastView.StateParcel(
            searchText = state.searchText,
            isSearchToggled = state.isSearchToggled,
            forceNet = state.forceNet,
            startRefreshing = state.startRefreshing,
            stopRefreshing = state.stopRefreshing
        )
        Timber.d("updateStateParcel: $stateParcel")

        viewModel.send(
            WeatherForecastView
                .Event
                .OnStateParcelUpdated
        )
    }

    private fun disableSearchView(renderEvent: WeatherForecastView.RenderEvent.DisableSearchView) {
        Timber.d("disableSearchView")

        binding.weatherForecastLocationSearchButton.toVisible()

        binding.weatherForecastSearchView.apply {
            toInvisible()
            setQuery(renderEvent.text, false)
        }

        binding.autoCompleteRecyclerView.apply {
            adapter = null
            toInvisible()
        }

        viewModel.send(
            WeatherForecastView
                .Event
                .OnSearchViewDismissed
        )
    }

    private fun enableSearchView() {
        autoCompleteAdapter.clearList()

        binding.weatherForecastSearchView.apply {
            toVisible()
            isFocusable = true
            isIconified = false
            requestFocusFromTouch()
        }

        binding.autoCompleteRecyclerView
            .apply {
                adapter = autoCompleteAdapter
                layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
                drawable?.let { res: Drawable ->
                    itemDecoration
                        .setDrawable(res)
                }
                addItemDecoration(itemDecoration)
                hasFixedSize()
                visibility = View.VISIBLE
            }

        binding.weatherForecastLocationSearchButton.visibility = View.INVISIBLE
    }

    private fun displayWeatherForecast(renderEvent: WeatherForecastView.RenderEvent.DisplayWeatherForecast) {
        Timber.d("Rendering weather forecast data")
        weatherForecastAdapter.updateList(renderEvent.list)
        setActionBarLocalityName(renderEvent.locality)

        viewModel.send(
            WeatherForecastView
                .Event
                .OnWeatherListDisplayed
        )
    }

    private fun onRequestLocationPermission(): Unit =
        onLocationPermissionGrantedWithPermissionCheck()

    @NeedsPermission(value = [Manifest.permission.ACCESS_FINE_LOCATION])
    fun onLocationPermissionGranted() {
        viewModel
            .send(
                WeatherForecastView
                    .Event
                    .OnLocationPermissionGranted
            )
    }

    @OnPermissionDenied(value = [Manifest.permission.ACCESS_FINE_LOCATION])
    fun onLocationPermissionDenied() {
        viewModel
            .send(
                WeatherForecastView
                    .Event
                    .OnLocationPermissionDenied
            )
    }

    @OnNeverAskAgain(value = [Manifest.permission.ACCESS_FINE_LOCATION])
    fun onLocationPermissionNeverAskAgain() {
        viewModel
            .send(
                WeatherForecastView
                    .Event
                    .OnLocationPermissionDeniedNeverAskAgain
            )
    }

    private fun renderError(errorCode: Int) {
        // TODO snack(errorCode)
        Timber.e("Rendering error code: ${getString(errorCode)}")
        viewModel
            .send(
                WeatherForecastView
                    .Event
                    .OnFailureHandled
            )
    }

    private fun hideActionBarLocalityName() =
        (requireActivity() as MainActivity)
            .hideLocalityActionBar()

    private fun setActionBarLocalityName(locality: Locality) =
        (requireActivity() as MainActivity)
            .setLocalityActionBar(locality)
}