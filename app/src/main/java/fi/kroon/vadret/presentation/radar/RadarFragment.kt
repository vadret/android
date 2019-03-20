package fi.kroon.vadret.presentation.radar

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.View
import com.jakewharton.rxbinding3.view.clicks
import com.jakewharton.rxbinding3.widget.changes
import com.jakewharton.rxbinding3.widget.userChanges
import com.squareup.picasso.Picasso
import com.squareup.picasso.Target
import fi.kroon.vadret.BuildConfig
import fi.kroon.vadret.R
import fi.kroon.vadret.presentation.BaseFragment
import fi.kroon.vadret.presentation.radar.di.RadarComponent
import fi.kroon.vadret.presentation.radar.di.RadarFeatureScope
import fi.kroon.vadret.presentation.weatherforecast.WeatherForecastFragment
import fi.kroon.vadret.utils.DEFAULT_BOUNDINGBOX_CENTER_LATITUDE
import fi.kroon.vadret.utils.DEFAULT_BOUNDINGBOX_CENTER_LONGITUDE
import fi.kroon.vadret.utils.DEFAULT_BOUNDINGBOX_LATITUDE_MAX
import fi.kroon.vadret.utils.DEFAULT_BOUNDINGBOX_LATITUDE_MIN
import fi.kroon.vadret.utils.DEFAULT_BOUNDINGBOX_LONGITUDE_MAX
import fi.kroon.vadret.utils.DEFAULT_BOUNDINGBOX_LONGITUDE_MIN
import fi.kroon.vadret.utils.DEFAULT_RADAR_FILE_EXTENSION
import fi.kroon.vadret.utils.DEFAULT_RADAR_ZOOM_LEVEL
import fi.kroon.vadret.utils.MAXIMUM_ZOOM_LEVEL
import fi.kroon.vadret.utils.MINIMUM_ZOOM_LEVEL
import fi.kroon.vadret.utils.RADAR_DEBOUNCE_MILLIS
import fi.kroon.vadret.utils.WIKIMEDIA_TILE_SOURCE_URL
import fi.kroon.vadret.utils.extensions.appComponent
import fi.kroon.vadret.utils.extensions.snack
import fi.kroon.vadret.utils.extensions.toObservable
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.subjects.PublishSubject
import kotlinx.android.synthetic.main.radar_fragment.*
import org.osmdroid.config.Configuration
import org.osmdroid.config.IConfigurationProvider
import org.osmdroid.tileprovider.tilesource.XYTileSource
import org.osmdroid.util.BoundingBox
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.overlay.GroundOverlay2
import org.threeten.bp.OffsetDateTime
import org.threeten.bp.format.DateTimeFormatter
import timber.log.Timber
import java.io.File
import java.util.concurrent.TimeUnit
import javax.inject.Inject

typealias RadarFile = fi.kroon.vadret.data.radar.model.File

@RadarFeatureScope
class RadarFragment : BaseFragment() {

    companion object {
        const val A_NAME = "wikimedia"
        const val A_ZOOM_MIN_LEVEL = 1
        const val A_ZOOM_MAX_LEVEL = 18
        const val A_TILE_SIZE_PIXELS = 256
        const val STATE_PARCEL_KEY = "STATE_PARCEL_KEY"
    }

    @Inject
    lateinit var viewModel: RadarViewModel

    @Inject
    lateinit var onViewInitialisedSubject: PublishSubject<RadarView.Event.OnViewInitialised>

    @Inject
    lateinit var onFailureHandledSubject: PublishSubject<RadarView.Event.OnFailureHandled>

    @Inject
    lateinit var onRadarImageDisplayedSubject: PublishSubject<RadarView.Event.OnRadarImageDisplayed>

    @Inject
    lateinit var onSeekBarStoppedSubject: PublishSubject<RadarView.Event.OnSeekBarStopped>

    @Inject
    lateinit var onStateParcelUpdatedSubject: PublishSubject<RadarView.Event.OnStateParcelUpdated>

    @Inject
    lateinit var onPlayButtonStartedSubject: PublishSubject<RadarView.Event.OnPlayButtonStarted>

    @Inject
    lateinit var onPlayButtonStoppedSubject: PublishSubject<RadarView.Event.OnPlayButtonStopped>

    @Inject
    lateinit var onSeekBarResetSubject: PublishSubject<RadarView.Event.OnSeekBarReset>

    @Inject
    lateinit var onPositionUpdatedSubject: PublishSubject<RadarView.Event.OnPositionUpdated>

    @Inject
    lateinit var onSeekBarRestoredSubject: PublishSubject<RadarView.Event.OnSeekBarRestored>

    @Inject
    lateinit var subscriptions: CompositeDisposable

    private lateinit var disposable: Disposable
    private var bundle: Bundle? = null
    private var stateParcel: RadarView.StateParcel? = null

    private val cmp: RadarComponent by lazy {
        appComponent()
            .radarComponentBuilder()
            .build()
    }

    private val defaultTileSource = XYTileSource(
        A_NAME,
        A_ZOOM_MIN_LEVEL,
        A_ZOOM_MAX_LEVEL,
        A_TILE_SIZE_PIXELS,
        DEFAULT_RADAR_FILE_EXTENSION,
        arrayOf(WIKIMEDIA_TILE_SOURCE_URL)
    )

    override fun layoutId(): Int = R.layout.radar_fragment

    override fun renderError(errorCode: Int) {
        snack(errorCode)
        Timber.e("Rendering error code: ${getString(errorCode)}")
        onFailureHandledSubject.onNext(
            RadarView
                .Event
                .OnFailureHandled
        )
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        Timber.d("-----BEGIN-----")
        Timber.d("ON ATTACH")
        cmp.inject(this)
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

    override fun onStop() {
        super.onStop()
        Timber.d("ON STOP")
        isConfigChangeOrProcessDeath = true
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        Timber.d("ON SAVE INSTANCE STATE")
        outState.apply {
            Timber.d("Saving instance: $stateParcel")
            Timber.d("-----END-----")
            putParcelable(WeatherForecastFragment.STATE_PARCEL_KEY, stateParcel)
        }
    }

    private fun setupMapViewConfiguration() {
        Timber.d("setupMapViewConfiguration")
        val configuration: IConfigurationProvider = Configuration.getInstance()
        configuration.run {
            osmdroidBasePath = File(context?.cacheDir!!.absolutePath, "osmdroid")
            osmdroidTileCache = File(configuration.osmdroidBasePath.absolutePath, "tile")
            userAgentValue = BuildConfig.APPLICATION_ID
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRadarMapView()
        setupEvents()
    }

    override fun onResume() {
        super.onResume()
        radarMapView.onResume()
        if (isConfigChangeOrProcessDeath) {
            setupEvents()
            isConfigChangeOrProcessDeath = false
        }
    }

    override fun onPause() {
        radarMapView.onPause()
        super.onPause()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        radarMapView.onDetach()
        disposeSeekBar()
        subscriptions.clear()
    }

    private fun disposeSeekBar() {
        if (::disposable.isInitialized) {
            disposable.dispose()
        }
    }

    private fun setupRadarMapView() {
        Timber.d("setupRadarMapView")
        radarMapView.apply {
            setTileSource(defaultTileSource)
            isTilesScaledToDpi = true
            setMultiTouchControls(true)
            setBuiltInZoomControls(false)
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
        if (subscriptions.size() == 0) {

            Observable.mergeArray(
                onViewInitialisedSubject
                    .toObservable(),
                onStateParcelUpdatedSubject
                    .toObservable(),
                onSeekBarStoppedSubject
                    .toObservable(),
                onRadarImageDisplayedSubject
                    .toObservable(),
                onSeekBarRestoredSubject
                    .toObservable(),
                onFailureHandledSubject
                    .toObservable(),
                onSeekBarResetSubject
                    .toObservable(),
                onPositionUpdatedSubject
                    .toObservable(),
                onPlayButtonStartedSubject
                    .toObservable(),
                onPlayButtonStoppedSubject
                    .toObservable(),
                radarSeekBar
                    .userChanges()
                    .throttleFirst(RADAR_DEBOUNCE_MILLIS, TimeUnit.MILLISECONDS)
                    .map { position: Int ->
                        RadarView
                            .Event
                            .OnPositionUpdated(
                                position
                            )
                    },
                radarSeekBar
                    .changes()
                    .skipInitialValue()
                    .map { position: Int ->
                        RadarView
                            .Event
                            .OnRadarImageDisplayed(position)
                    },
                radarPlayFab
                    .clicks()
                    .map {
                        RadarView
                            .Event
                            .OnPlayButtonClicked
                    }

            ).observeOn(
                schedulers.io()
            ).compose(
                viewModel()
            ).observeOn(
                schedulers.ui()
            ).subscribe(
                ::render
            ).addTo(
                subscriptions
            )

            onViewInitialisedSubject
                .onNext(
                    RadarView
                        .Event
                        .OnViewInitialised(
                            stateParcel = bundle?.getParcelable(
                                STATE_PARCEL_KEY
                            )
                        )
                )
        }
    }

    private fun updateStateParcel(state: RadarView.State) {
        Timber.d("Updating stateParcel")
        stateParcel = RadarView.StateParcel(
            isInitialised = state.isInitialised,
            currentSeekBarIndex = state.currentSeekBarIndex,
            isSeekBarRunning = state.isSeekBarRunning,
            seekBarMax = state.seekBarMax,
            timeStamp = state.timeStamp
        )

        onStateParcelUpdatedSubject
            .onNext(
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
        onSeekBarRestoredSubject.onNext(
            RadarView
                .Event
                .OnSeekBarRestored
        )
    }

    private fun resetSeekBarPosition(viewState: RadarView.State) {
        setRadarSeekBarPosition(viewState)
        Timber.d("resetSeekBarPosition")
        onSeekBarResetSubject.onNext(
            RadarView
                .Event
                .OnSeekBarReset
        )
    }

    private fun setRadarSeekBarPosition(viewState: RadarView.State) {
        radarSeekBar?.run {
            progress = viewState.currentSeekBarIndex
            max = viewState.seekBarMax
        }
    }

    private fun setPlayButtonToPlaying() {
        radarPlayFab.setImageResource(R.drawable.ic_pause_white_24dp)
        Timber.d("setPlayButtonToPlaying")
        onPlayButtonStartedSubject.onNext(
            RadarView
                .Event
                .OnPlayButtonStarted
        )
    }

    private fun setPlayButtonToStopped() {
        radarPlayFab.setImageResource(R.drawable.ic_play_arrow_white_24dp)
        Timber.d("setPlayButtonToStopped")
        onPlayButtonStoppedSubject.onNext(
            RadarView
                .Event
                .OnPlayButtonStopped
        )
    }

    private fun startSeekBar(state: RadarView.State) {
        disposable = Observable.interval(
            RADAR_DEBOUNCE_MILLIS,
            TimeUnit.MILLISECONDS
        ).map { _ ->
            radarSeekBar?.run {
                progress += state.seekStep
                max = state.seekBarMax
            }
        }.subscribe()
    }

    private fun stopSeekBar() {
        disposeSeekBar()
        onSeekBarStoppedSubject.onNext(
            RadarView
                .Event
                .OnSeekBarStopped
        )
    }

    private fun displayRadarImage(file: RadarFile) =
        Picasso
            .get()
            .load(
                file
                    .formats
                    .first()
                    .link
            )
            .into(
                object : Target {
                    override fun onPrepareLoad(placeHolderDrawable: Drawable?) {}
                    override fun onBitmapFailed(e: Exception?, errorDrawable: Drawable?) {
                        Timber.e("Image loading failed.")
                    }

                    override fun onBitmapLoaded(bitmap: Bitmap?, from: Picasso.LoadedFrom?) {
                        Timber.d("Image loaded successfully.")
                        radarDate?.apply {
                            text = OffsetDateTime.parse(file.formats.first().updated).format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
                        }
                        bitmap?.let { image: Bitmap ->
                            setOverlayImage(image)
                        }
                    }
                }
            )

    private fun setOverlayImage(bitmap: Bitmap) {
        val radarOverlay: GroundOverlay2 = GroundOverlay2()
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

        radarMapView?.apply {
            overlays.clear()
            overlayManager.add(radarOverlay)
            invalidate()
        }
    }
}