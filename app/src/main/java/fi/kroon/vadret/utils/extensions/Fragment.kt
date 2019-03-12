package fi.kroon.vadret.utils.extensions

import android.content.Context
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar
import fi.kroon.vadret.BaseApplication

fun Fragment.hideKeyboard() {
    val imm = requireContext()
        .getSystemService(Context.INPUT_METHOD_SERVICE)
        as InputMethodManager

    imm.hideSoftInputFromWindow(view?.windowToken, 0)
}

fun Context.toToast(message: String, duration: Int = Toast.LENGTH_SHORT) = Toast.makeText(this, message, duration).show()
fun Fragment.appComponent() = BaseApplication.appComponent(requireContext().applicationContext)

inline fun Fragment.snack(@StringRes messageRes: Int, length: Int = Snackbar.LENGTH_LONG, init: Snackbar.() -> Unit = {}) {
    return view!!.snack(context!!.getString(messageRes), length, init)
}