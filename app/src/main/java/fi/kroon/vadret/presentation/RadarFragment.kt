package fi.kroon.vadret.presentation

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.View
import android.widget.SeekBar
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.squareup.picasso.Picasso
import com.squareup.picasso.Target
import fi.kroon.vadret.R
import fi.kroon.vadret.data.DEFAULT_RADAR_FILE_EXTENSION
import fi.kroon.vadret.data.DEFAULT_BOUNDINGBOX_CENTER_LONGITUDE
import fi.kroon.vadret.data.DEFAULT_BOUNDINGBOX_CENTER_LATITUDE
import fi.kroon.vadret.data.DEFAULT_LONGITUDE_MIN
import fi.kroon.vadret.data.DEFAULT_LONGITUDE_MAX
import fi.kroon.vadret.data.DEFAULT_LATITUDE_MIN
import fi.kroon.vadret.data.DEFAULT_LATITUDE_MAX
import fi.kroon.vadret.data.MAXIMUM_ZOOM_LEVEL
import fi.kroon.vadret.data.MINIMUM_ZOOM_LEVEL
import fi.kroon.vadret.data.DEFAULT_ZOOM_LEVEL
import fi.kroon.vadret.data.DEFAULT_TILE_SOURCE_URL
import fi.kroon.vadret.data.DEFAULT_RADAR_INTERVAL
import fi.kroon.vadret.data.OFFSET
import fi.kroon.vadret.data.exception.Either
import fi.kroon.vadret.data.exception.Failure
import fi.kroon.vadret.data.radar.RadarRequest
import fi.kroon.vadret.data.radar.exception.RadarFailure
import fi.kroon.vadret.data.radar.model.File
import fi.kroon.vadret.data.radar.model.Radar
import fi.kroon.vadret.presentation.viewmodel.RadarViewModel
import fi.kroon.vadret.utils.Schedulers
import fi.kroon.vadret.utils.extensions.viewModel
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import io.reactivex.rxkotlin.addTo
import kotlinx.android.synthetic.main.radar_fragment.radarDate
import org.osmdroid.tileprovider.tilesource.XYTileSource
import org.osmdroid.util.BoundingBox
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.GroundOverlay2
import org.threeten.bp.OffsetDateTime
import org.threeten.bp.format.DateTimeFormatter
import timber.log.Timber
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class RadarFragment : BaseFragment() {

    companion object {
        const val REQUEST_WRITE_EXTERNAL_STORAGE: Int = 1
    }

    override fun layoutId() = R.layout.radar_fragment

    @Inject
    lateinit var schedulers: Schedulers

    lateinit var playRadarDisposable: Disposable

    private var isToggled: Boolean = false
    private lateinit var radarViewModel: RadarViewModel
    private lateinit var mapView: MapView
    private lateinit var radarSeekbar: SeekBar
    private lateinit var radarPlay: FloatingActionButton

    private val DEFAULT_TILE_SOURCE = XYTileSource("wikimedia", 1, 18, 256, DEFAULT_RADAR_FILE_EXTENSION, arrayOf(DEFAULT_TILE_SOURCE_URL))

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        cmp.inject(this)
        radarViewModel = viewModel(viewModelFactory) {}
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initialiseMapView()
        requestStoragePermission()
        loadRadar(RadarRequest())
    }

    override fun onResume() {
        super.onResume()
        mapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        mapView.onPause()
        pause()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mapView.onDetach()
    }

    private fun initialiseMapView() {
        mapView = view?.findViewById(R.id.mapView)!!
        mapView.setTileSource(DEFAULT_TILE_SOURCE)
        mapView.isTilesScaledToDpi = true
        mapView.setMultiTouchControls(true)
        mapView.setBuiltInZoomControls(false)
        mapView.maxZoomLevel = MAXIMUM_ZOOM_LEVEL
        mapView.minZoomLevel = MINIMUM_ZOOM_LEVEL
        mapView.controller.setCenter(GeoPoint(DEFAULT_BOUNDINGBOX_CENTER_LATITUDE, DEFAULT_BOUNDINGBOX_CENTER_LONGITUDE))
        mapView.controller.setZoom(DEFAULT_ZOOM_LEVEL)
        mapView.setScrollableAreaLimitDouble(BoundingBox(DEFAULT_LATITUDE_MAX, DEFAULT_LONGITUDE_MAX, DEFAULT_LATITUDE_MIN, DEFAULT_LONGITUDE_MIN))
    }

    private fun loadRadar(radarRequest: RadarRequest) {
        radarViewModel
            .get(radarRequest)
            .subscribeOn(schedulers.io())
            .observeOn(schedulers.ui())
            .onErrorReturn { Either.Left(RadarFailure.NoRadarAvailable()) }
            .subscribe(::radarHandler)
            .addTo(subscriptions)
    }

    private fun radarHandler(data: Either<Failure, Radar>) {
        data.either(::handleFailure, ::handleRadar)
    }

    private fun handleFailure(failure: Failure?) {
        when (failure) {
            is Failure.IOException -> renderFailure(R.string.io_exception)
            is RadarFailure.NoRadarAvailable -> renderFailure(R.string.no_radar_available)
            is Failure.NetworkException -> renderFailure(R.string.network_failure)
            is Failure.NetworkOfflineFailure -> renderFailure(R.string.no_network_available)
        }
    }

    private fun handleRadar(radar: Radar) {
        radarSeekbar = view?.findViewById(R.id.radarSeekBar)!!
        radarSeekbar.max = radar.files.size - OFFSET
        radarSeekbar.setOnSeekBarChangeListener(
            object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                    Timber.d("onProgressChanged: ${radar.files[progress]}")
                    if (::playRadarDisposable.isInitialized)
                        renderRadar(radar.files[progress])
                }

                override fun onStartTrackingTouch(seekBar: SeekBar?) {
                    Timber.d("Tracking started.")
                    startNoAutoPlay()
                }

                override fun onStopTrackingTouch(seekBar: SeekBar?) {
                    Timber.d("Tracking stopped.")
                    pauseNoAutoPlay()
                }
            }
        )
        radarPlay = view?.findViewById(R.id.radarPlay)!!
        radarPlay.setOnClickListener {
            Timber.d("Clicked nextRadar!")
            if (isToggled) {
                pause()
            } else {
                start()
            }
        }
    }

    private fun pauseNoAutoPlay() {
        if (!isToggled) {
            if (::playRadarDisposable.isInitialized)
                playRadarDisposable.dispose()
            radarPlay.setImageResource(R.drawable.ic_play_arrow_white_24dp)
        }
    }

    private fun startNoAutoPlay() {
        if (!isToggled) {
            radarPlay.setImageResource(R.drawable.ic_pause_white_24dp)
            playRadarDisposable = playRadar()
                .debounce(300, TimeUnit.MILLISECONDS)
                .subscribe()
        }
    }

    private fun pause() {
        if (::playRadarDisposable.isInitialized)
            playRadarDisposable.dispose()
        isToggled = false
        radarPlay.setImageResource(R.drawable.ic_play_arrow_white_24dp)
    }

    private fun start() {
        isToggled = true
        radarPlay.setImageResource(R.drawable.ic_pause_white_24dp)
        playRadarDisposable = playRadar()
            .subscribe()
    }

    private fun playRadar() = Observable
        .interval(DEFAULT_RADAR_INTERVAL, TimeUnit.MILLISECONDS)
        .map { nextRadar() }

    private fun nextRadar() =
        if (radarSeekbar.progress < radarSeekbar.max && radarSeekbar.progress >= 0) {
            radarSeekbar.progress += 1
            Timber.d("Progress: ${radarSeekbar.progress}")
        } else {
            radarSeekbar.progress = 0
            Timber.d("Progress: ${radarSeekbar.progress}")
        }

    private fun renderRadar(file: File) {

        Timber.d("Radar data: $file")
        Picasso.get().load(file.formats[0].link).into(
            object : Target {
                override fun onPrepareLoad(placeHolderDrawable: Drawable?) {}
                override fun onBitmapFailed(e: Exception?, errorDrawable: Drawable?) {
                    Timber.d("Image loading failed.")
                }
                override fun onBitmapLoaded(bitmap: Bitmap?, from: Picasso.LoadedFrom?) {
                    if (playRadarDisposable.isDisposed or subscriptions.isDisposed) {
                        Timber.d("Caller disposed assets. Return")
                        return
                    }

                    mapView.overlays.clear()
                    radarDate.text = OffsetDateTime.parse(file.formats[0].updated).format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
                    Timber.d("Image loaded successfully.")
                    val radarOverlay = GroundOverlay2()
                    radarOverlay.image = bitmap
                    radarOverlay.setPosition(
                        GeoPoint(DEFAULT_LATITUDE_MAX, DEFAULT_LONGITUDE_MIN),
                        GeoPoint(DEFAULT_LATITUDE_MIN, DEFAULT_LONGITUDE_MAX)
                    )
                    mapView.overlayManager.add(radarOverlay)
                    mapView.invalidate()
                }
            }
        )
    }

    private fun renderFailure(@StringRes message: Int) = Snackbar.make(view!!, message, Snackbar.LENGTH_LONG).show()

    private fun requestStoragePermission() {

        Timber.d("Requesting permission begin")
        if (ContextCompat.checkSelfPermission(context!!,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
            != PackageManager.PERMISSION_GRANTED) {
            Timber.d("Requesting: ${android.Manifest.permission.WRITE_EXTERNAL_STORAGE}")
            requestPermissions(
                arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE),
                RadarFragment.REQUEST_WRITE_EXTERNAL_STORAGE
            )
        } else {
            Timber.d("Permission is already granted. Proceeding.")
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when (requestCode) {
            WeatherFragment.REQUEST_ACCESS_FINE_LOCATION -> {
                // If request is cancelled, the result arrays are empty.
                Timber.d("RadarFragment Request Code: ${RadarFragment.REQUEST_WRITE_EXTERNAL_STORAGE}")
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    Timber.d("Permission granted.")
                    Toast.makeText(this.context, R.string.permission_granted, Toast.LENGTH_LONG).show()
                } else {
                    Timber.d("Permission denied.")
                    Toast.makeText(this.context, R.string.permission_missing, Toast.LENGTH_LONG).show()
                }
                return
            }
            else -> {
                super.onRequestPermissionsResult(requestCode, permissions, grantResults)
            }
        }
    }
}