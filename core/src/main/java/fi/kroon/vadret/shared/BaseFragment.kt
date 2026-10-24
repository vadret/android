package fi.kroon.vadret.shared

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

    var isConfigChangeOrProcessDeath = false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        Timber.d("ON CREATE VIEW")
        return inflater.inflate(layoutId(), container, false)
    }

    fun hideActionBarLocalityName() =
        (requireActivity() as ILocalityActionBar)
            .hideLocalityActionBar()

    fun setActionBarLocalityName(locality: Locality) =
        (requireActivity() as ILocalityActionBar)
            .setLocalityActionBar(locality)
}