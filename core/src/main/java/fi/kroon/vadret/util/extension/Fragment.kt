package fi.kroon.vadret.util.extension

import android.content.Context
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar

fun Fragment.hideKeyboard() {
    val imm = requireContext()
        .getSystemService(Context.INPUT_METHOD_SERVICE)
        as InputMethodManager

    imm.hideSoftInputFromWindow(view?.windowToken, 0)
}

fun Context.toast(message: String, duration: Int = Toast.LENGTH_SHORT) = Toast.makeText(this, message, duration).show()

inline fun Fragment.snack(@StringRes messageRes: Int, length: Int = Snackbar.LENGTH_LONG, init: Snackbar.() -> Unit = {}) {
    return view!!.snack(context!!.getString(messageRes), length, init)
}