package fi.kroon.vadret.presentation

import androidx.appcompat.app.AppCompatActivity
import fi.kroon.vadret.R
import fi.kroon.vadret.data.nominatim.model.Locality
import fi.kroon.vadret.utils.extensions.toGone
import fi.kroon.vadret.utils.extensions.toVisible
import kotlinx.android.synthetic.main.activity_main.*

abstract class BaseActivity : AppCompatActivity() {

    abstract fun renderError(errorCode: Int)

    fun hideLocalityActionBar() = currentLocationName.toGone()

    fun setLocalityActionBar(locality: Locality) {
        locality.name?.let {
            currentLocationName.text = locality.name
        } ?: currentLocationName.setText(R.string.unknown_area)
        currentLocationName.toVisible()
    }
}