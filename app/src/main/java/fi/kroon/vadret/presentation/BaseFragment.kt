package fi.kroon.vadret.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import fi.kroon.vadret.data.nominatim.model.Locality
import timber.log.Timber

abstract class BaseFragment : Fragment() {

    abstract fun layoutId(): Int

    abstract fun renderError(errorCode: Int)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        Timber.d("ON CREATE VIEW")
        return inflater.inflate(layoutId(), container, false)
    }

    fun hideActionBarLocalityName() =
        (requireActivity() as MainActivity)
            .disableLocalityActionBar()

    fun displayActionBarLocalityName(locality: Locality) =
        (requireActivity() as MainActivity)
            .displayLocalityActionBar(locality)
}