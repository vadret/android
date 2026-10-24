package fi.kroon.vadret.radar

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.graphics.drawable.toBitmap
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import coil.ImageLoader
import coil.request.ImageRequest
import fi.kroon.vadret.core.R
import fi.kroon.vadret.radar.databinding.RadarFragmentBinding
import fi.kroon.vadret.radar.di.DaggerRadarComponent
import fi.kroon.vadret.radar.di.RadarComponent
import fi.kroon.vadret.util.DEFAULT_BOUNDINGBOX_CENTER_LATITUDE
import fi.kroon.vadret.util.DEFAULT_BOUNDINGBOX_CENTER_LONGITUDE
import fi.kroon.vadret.util.DEFAULT_BOUNDINGBOX_LATITUDE_MAX
import fi.kroon.vadret.util.DEFAULT_BOUNDINGBOX_LATITUDE_MIN
import fi.kroon.vadret.util.DEFAULT_BOUNDINGBOX_LONGITUDE_MAX
import fi.kroon.vadret.util.DEFAULT_BOUNDINGBOX_LONGITUDE_MIN
import fi.kroon.vadret.util.DEFAULT_RADAR_FILE_EXTENSION
import fi.kroon.vadret.util.DEFAULT_RADAR_ZOOM_LEVEL
import fi.kroon.vadret.util.MAXIMUM_ZOOM_LEVEL
import fi.kroon.vadret.util.MINIMUM_ZOOM_LEVEL
import fi.kroon.vadret.util.RADAR_DEBOUNCE_MILLIS
import fi.kroon.vadret.util.WIKIMEDIA_TILE_SOURCE_URL
import fi.kroon.vadret.util.extension.coreComponent
import fi.kroon.vadret.util.extension.lazyAndroid
import fi.kroon.vadret.util.extension.snack
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import org.osmdroid.config.Configuration
import org.osmdroid.config.IConfigurationProvider
import org.osmdroid.tileprovider.tilesource.XYTileSource
import org.osmdroid.util.BoundingBox
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.CustomZoomButtonsController
import org.osmdroid.views.overlay.GroundOverlay
import ru.ldralighieri.corbind.view.clicks
import ru.ldralighieri.corbind.widget.changes
import timber.log.Timber
import java.io.File

typealias RadarFile = fi.kroon.vadret.data.radar.model.File

class RadarFragment : Fragment() {

    private companion object {
        const val A_NAME = "wikimedia"
        const val A_ZOOM_MIN_LEVEL = 1
        const val A_ZOOM_MAX_LEVEL = 18
        const val A_TILE_SIZE_PIXELS = 256
        const val STATE_PARCEL_KEY = "STATE_PARCEL_KEY"
    }

    private var _binding: RadarFragmentBinding? = null
    private val binding: RadarFragmentBinding get() = _binding!!
    private var seekBarJob: Job? = null
    private var bundle: Bundle? = null
    private var stateParcel: RadarView.StateParcel? = null

    private val component: RadarComponent by lazyAndroid {
        DaggerRadarComponent
            .factory()
            .create(context = requireContext(), coreComponent = coreComponent)
    }

    private val imageLoader: ImageLoader by lazyAndroid {
        component.provideImageLoader()
    }

    private val viewModel: RadarViewModel by lazyAndroid {
        component.provideRadarViewModel()
    }

    private val defaultTileSource = XYTileSource(
        A_NAME,
        A_ZOOM_MIN_LEVEL,
        A_ZOOM_MAX_LEVEL,
        A_TILE_SIZE_PIXELS,
        DEFAULT_RADAR_FILE_EXTENSION,
        arrayOf(WIKIMEDIA_TILE_SOURCE_URL)
    )

    private fun renderError(errorCode: Int) {
        snack(errorCode)
        Timber.e("Rendering error code: ${getString(errorCode)}")
        viewModel.send(
            RadarView
                .Event
                .OnFailureHandled
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupMapViewConfiguration()
        savedInstanceState?.let { restoredBundle ->
            if (bundle == null) {
                Timber.d("savedInstanceState restored: $restoredBundle")
                bundle = restoredBundle
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = RadarFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRadarMapView()
        setupEvents()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        Timber.d("ON SAVE INSTANCE STATE")
        outState.apply {
            Timber.d("Saving instance: $stateParcel")
            Timber.d("-----END-----")
            putParcelable(STATE_PARCEL_KEY, stateParcel)
        }
    }

    private fun setupMapViewConfiguration() {
        Timber.d("setupMapViewConfiguration")
        val configuration: IConfigurationProvider = Configuration.getInstance()
        configuration.run {
            osmdroidBasePath = File(context?.cacheDir!!.absolutePath, "osmdroid")
            osmdroidTileCache = File(configuration.osmdroidBasePath.absolutePath, "tile")
            userAgentValue = requireContext().packageName
        }
    }

    override fun onResume() {
        super.onResume()
        binding.radarMapView.onResume()
    }

    override fun onPause() {
        binding.radarMapView.onPause()
        super.onPause()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.radarMapView.onDetach()
        disposeSeekBar()
        _binding = null
    }

    private fun disposeSeekBar() {
        seekBarJob?.cancel()
        seekBarJob = null
    }

    private fun setupRadarMapView() {
        Timber.d("setupRadarMapView")
        binding.radarMapView.apply {
            setTileSource(defaultTileSource)
            isTilesScaledToDpi = true
            setMultiTouchControls(true)
            zoomController.setVisibility(CustomZoomButtonsController.Visibility.NEVER)
            maxZoomLevel = MAXIMUM_ZOOM_LEVEL
            minZoomLevel = MINIMUM_ZOOM_LEVEL
            controller.setCenter(
                GeoPoint(
                    DEFAULT_BOUNDINGBOX_CENTER_LATITUDE,
                    DEFAULT_BOUNDINGBOX_CENTER_LONGITUDE
                )
            )
            controller.setZoom(DEFAULT_RADAR_ZOOM_LEVEL)
            setScrollableAreaLimitDouble(
                BoundingBox(
                    DEFAULT_BOUNDINGBOX_LATITUDE_MAX,
                    DEFAULT_BOUNDINGBOX_LONGITUDE_MAX,
                    DEFAULT_BOUNDINGBOX_LATITUDE_MIN,
                    DEFAULT_BOUNDINGBOX_LONGITUDE_MIN
                )
            )
        }
    }

    private fun setupEvents() {
        viewModel
            .viewState
            .onEach(::render)
            .launchIn(viewLifecycleOwner.lifecycleScope)

        /**
         *  Every progress change, from the user or from playback,
         *  displays the image at that position.
         */
        binding.radarSeekBar
            .changes()
            .dropInitialValue()
            .onEach { position: Int ->
                viewModel.send(
                    RadarView
                        .Event
                        .OnRadarImageDisplayed(position)
                )
            }.launchIn(viewLifecycleOwner.lifecycleScope)

        binding.radarPlayFab
            .clicks()
            .onEach {
                viewModel.send(
                    RadarView
                        .Event
                        .OnPlayButtonClicked
                )
            }.launchIn(viewLifecycleOwner.lifecycleScope)

        viewModel.send(
            RadarView
                .Event
                .OnViewInitialised(
                    stateParcel = bundle?.getParcelable(
                        STATE_PARCEL_KEY
                    )
                )
        )
    }

    private fun updateStateParcel(state: RadarView.State) {
        Timber.d("Updating stateParcel")
        stateParcel = RadarView.StateParcel(
            isInitialised = state.isInitialised,
            currentSeekBarIndex = state.currentSeekBarIndex,
            isSeekBarRunning = state.isSeekBarRunning,
            seekBarMax = state.seekBarMax
        )

        viewModel.send(
            RadarView
                .Event
                .OnStateParcelUpdated
        )
    }

    private fun render(viewState: RadarView.State) {
        when (viewState.renderEvent) {
            RadarView.RenderEvent.None -> Unit
            RadarView.RenderEvent.UpdateStateParcel -> updateStateParcel(viewState)
            RadarView.RenderEvent.SetPlayButtonToPlaying -> setPlayButtonToPlaying()
            RadarView.RenderEvent.SetPlayButtonToStopped -> setPlayButtonToStopped()
            RadarView.RenderEvent.StartSeekBar -> startSeekBar(viewState)
            RadarView.RenderEvent.StopSeekBar -> stopSeekBar()
            is RadarView.RenderEvent.DisplayError -> renderError(viewState.renderEvent.errorCode)
            is RadarView.RenderEvent.DisplayRadarImage -> displayRadarImage(viewState.renderEvent.file)
            RadarView.RenderEvent.ResetSeekBar -> resetSeekBarPosition(viewState)
            RadarView.RenderEvent.RestoreSeekBarPosition -> restoreSeekBarPosition(viewState)
        }
    }

    private fun restoreSeekBarPosition(viewState: RadarView.State) {
        setRadarSeekBarPosition(viewState)
        viewModel.send(
            RadarView
                .Event
                .OnSeekBarRestored
        )
    }

    private fun resetSeekBarPosition(viewState: RadarView.State) {
        setRadarSeekBarPosition(viewState)
        Timber.d("resetSeekBarPosition")
        viewModel.send(
            RadarView
                .Event
                .OnSeekBarReset
        )
    }

    private fun setRadarSeekBarPosition(viewState: RadarView.State) {
        binding.radarSeekBar.run {
            progress = viewState.currentSeekBarIndex
            max = viewState.seekBarMax
        }
    }

    private fun setPlayButtonToPlaying() {
        binding.radarPlayFab.setImageResource(R.drawable.ic_pause_white_24dp)
        Timber.d("setPlayButtonToPlaying")
        viewModel.send(
            RadarView
                .Event
                .OnPlayButtonStarted
        )
    }

    private fun setPlayButtonToStopped() {
        binding.radarPlayFab.setImageResource(R.drawable.ic_play_arrow_white_24dp)
        Timber.d("setPlayButtonToStopped")
        viewModel.send(
            RadarView
                .Event
                .OnPlayButtonStopped
        )
    }

    private fun startSeekBar(state: RadarView.State) {
        disposeSeekBar()
        seekBarJob = viewLifecycleOwner.lifecycleScope.launch {
            while (isActive) {
                delay(RADAR_DEBOUNCE_MILLIS)
                binding.radarSeekBar.run {
                    progress += state.seekStep
                    max = state.seekBarMax
                }
            }
        }
    }

    private fun stopSeekBar() {
        disposeSeekBar()
        viewModel.send(
            RadarView
                .Event
                .OnSeekBarStopped
        )
    }

    private fun displayRadarImage(file: RadarFile) {
        ImageRequest
            .Builder(requireContext())
            .data(
                file
                    .formats
                    .first()
                    .link
            )
            .target(
                onStart = { _ ->
                    Unit
                },
                onSuccess = { result: Drawable ->
                    setOverlayImage(result.toBitmap())
                },
                onError = { _ ->
                    Timber.e("Image loading failed.")
                }
            )
            .build()
            .apply {
                imageLoader.enqueue(this)
            }
    }

    private fun setOverlayImage(bitmap: Bitmap) {
        val radarOverlay: GroundOverlay = GroundOverlay()
            .apply {

                image = bitmap
                setPosition(
                    GeoPoint(
                        DEFAULT_BOUNDINGBOX_LATITUDE_MAX,
                        DEFAULT_BOUNDINGBOX_LONGITUDE_MIN
                    ),
                    GeoPoint(
                        DEFAULT_BOUNDINGBOX_LATITUDE_MIN,
                        DEFAULT_BOUNDINGBOX_LONGITUDE_MAX
                    )
                )
            }

        binding.radarMapView.apply {
            overlays.clear()
            overlayManager.add(radarOverlay)
            invalidate()
        }
    }
}