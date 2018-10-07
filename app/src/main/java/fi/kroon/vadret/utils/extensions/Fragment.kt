package fi.kroon.vadret.utils.extensions

import android.content.Context
import android.os.Bundle
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders

inline fun <reified T : ViewModel> Fragment.viewModel(factory: ViewModelProvider.Factory, body: T.() -> Unit): T {
    val vm = ViewModelProviders.of(this, factory)[T::class.java]
    vm.body()
    return vm
}

inline fun <T : Fragment> T.withArguments(
    argsBuilder: Bundle.() -> Unit
): T = this.apply {
        arguments = Bundle().apply(argsBuilder)
    }

fun Fragment.hideKeyboard() {
    val imm = requireContext()
        .getSystemService(Context.INPUT_METHOD_SERVICE)
        as InputMethodManager

    imm.hideSoftInputFromWindow(view?.windowToken, 0)
}

fun Context.toToast(message: String, duration: Int = Toast.LENGTH_SHORT) = Toast.makeText(this, message, duration).show()