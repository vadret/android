package fi.kroon.vadret.presentation.dialog

import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import fi.kroon.vadret.VadretApplication
import fi.kroon.vadret.di.component.VadretApplicationComponent
import io.reactivex.disposables.CompositeDisposable
import javax.inject.Inject

abstract class BaseDialog : DialogFragment() {

    protected val subscriptions = CompositeDisposable()

    val cmp: VadretApplicationComponent by lazy(mode = LazyThreadSafetyMode.NONE) {
        (activity?.application as VadretApplication).cmp
    }

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    override fun onDestroy() {
        subscriptions.clear()
        super.onDestroy()
    }
}