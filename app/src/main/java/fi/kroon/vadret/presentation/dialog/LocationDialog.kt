package fi.kroon.vadret.presentation.dialog

import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import fi.kroon.vadret.R
import fi.kroon.vadret.data.exception.Either
import fi.kroon.vadret.data.exception.Failure
import fi.kroon.vadret.data.nominatim.model.Nominatim
import fi.kroon.vadret.data.nominatim.model.NominatimList
import fi.kroon.vadret.presentation.viewmodel.SharedPreferencesViewModel
import fi.kroon.vadret.utils.Schedulers
import fi.kroon.vadret.utils.extensions.splitBySpaceTakeFirst
import fi.kroon.vadret.utils.extensions.toFilteredNominatimArray
import fi.kroon.vadret.utils.extensions.toFilteredStringArray
import fi.kroon.vadret.utils.extensions.viewModel
import io.reactivex.rxkotlin.addTo
import timber.log.Timber
import javax.inject.Inject

class LocationDialog : BaseDialog() {

    var position: Int = -1

    @Inject
    lateinit var schedulers: Schedulers

    private lateinit var sharedPreferencesViewModel: SharedPreferencesViewModel

    lateinit var onDialogDismissed: () -> Unit

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        cmp.inject(this)
        sharedPreferencesViewModel = viewModel(viewModelFactory) {}

        val nominatimList = arguments?.getParcelable<NominatimList>("nominatim")

        nominatimList?.let { _ ->

            val itemList = nominatimList.toFilteredNominatimArray()
            val itemDisplayNamelist = nominatimList.toFilteredStringArray()

            return activity?.let {
                    val dialogClickListener = DialogInterface.OnClickListener { dialog, which ->
                        setManualLocationMode(itemList[which])
                        Timber.d("Dialog: $dialog, which: $which")
                    }
                    AlertDialog.Builder(it)
                        .setTitle(R.string.select_a_location)
                        .setSingleChoiceItems(itemDisplayNamelist, position, dialogClickListener)
                        .setPositiveButton(R.string.select) { d, _ ->
                            d.dismiss()
                            onDialogDismissed()
                        }.create()
                } as Dialog
        }
        return activity?.let {
            AlertDialog.Builder(it)
                .setTitle(R.string.search_results)
                .setMessage(R.string.no_results)
                .setPositiveButton(R.string.ok) { d, _ -> d.dismiss() }
                .create()
        } as Dialog
    }

    private fun setManualLocationMode(nominatim: Nominatim) {
        nominatim.address.let {
            if (it?.city != null && it.state != null) {
                Timber.d("Saving: ${nominatim.address?.city}, ${nominatim.address?.state?.splitBySpaceTakeFirst()}")
                saveSetting(getString(R.string.use_gps_by_defeault_key), false)
                saveSetting(getString(R.string.latitude_key), nominatim.lat)
                saveSetting(getString(R.string.longitude_key), nominatim.lon)
                saveSetting(getString(R.string.city_key), nominatim.address!!.city!!)
                saveSetting(getString(R.string.province_key), nominatim.address.state!!.splitBySpaceTakeFirst())
            } else if (it?.hamlet != null && it.state != null) {
                Timber.d("Saving: ${nominatim.address?.hamlet}, ${nominatim.address?.state?.splitBySpaceTakeFirst()}")
                saveSetting(getString(R.string.use_gps_by_defeault_key), false)
                saveSetting(getString(R.string.latitude_key), nominatim.lat)
                saveSetting(getString(R.string.longitude_key), nominatim.lon)
                saveSetting(getString(R.string.city_key), nominatim.address!!.hamlet!!)
                saveSetting(getString(R.string.province_key), nominatim.address.state!!.splitBySpaceTakeFirst())
            } else if (it?.village != null && it.state != null) {
                Timber.d("Saving: ${nominatim.address?.village}, ${nominatim.address?.state?.splitBySpaceTakeFirst()}")
                saveSetting(getString(R.string.use_gps_by_defeault_key), false)
                saveSetting(getString(R.string.latitude_key), nominatim.lat)
                saveSetting(getString(R.string.longitude_key), nominatim.lon)
                saveSetting(getString(R.string.city_key), nominatim.address!!.village!!)
                saveSetting(getString(R.string.province_key), nominatim.address.state!!.splitBySpaceTakeFirst())
            } else if (it?.town != null && it.state != null) {
                Timber.d("Saving: ${nominatim.address?.town}, ${nominatim.address?.state?.splitBySpaceTakeFirst()}")
                saveSetting(getString(R.string.use_gps_by_defeault_key), false)
                saveSetting(getString(R.string.latitude_key), nominatim.lat)
                saveSetting(getString(R.string.longitude_key), nominatim.lon)
                saveSetting(getString(R.string.city_key), nominatim.address!!.town!!)
                saveSetting(getString(R.string.province_key), nominatim.address.state!!.splitBySpaceTakeFirst())
            } else {
                Timber.d("Failed to save settings because of missing values")
                Timber.d("Nominatim: $nominatim")
            }
        }
    }

    private fun saveSetting(key: String, value: Any) {
        when (value) {
            is Boolean -> sharedPreferencesViewModel.putBoolean(key, value)
                .subscribeOn(schedulers.io())
                .observeOn(schedulers.ui())
                .onErrorReturn { Either.Left(Failure.IOException()) }
                .subscribe()
                .addTo(subscriptions)
            is String -> sharedPreferencesViewModel.putString(key, value)
                .subscribeOn(schedulers.io())
                .observeOn(schedulers.ui())
                .onErrorReturn { Either.Left(Failure.IOException()) }
                .subscribe()
                .addTo(subscriptions)
            else -> Timber.e("Failed to save value: $value")
        }
    }
}