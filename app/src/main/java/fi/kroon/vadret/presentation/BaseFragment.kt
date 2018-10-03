package fi.kroon.vadret.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import fi.kroon.vadret.VadretApplication
import fi.kroon.vadret.di.component.VadretApplicationComponent
import io.reactivex.disposables.CompositeDisposable
import javax.inject.Inject

abstract class BaseFragment : Fragment() {

    abstract fun layoutId(): Int
    protected val subscriptions = CompositeDisposable()

    val cmp: VadretApplicationComponent by lazy(mode = LazyThreadSafetyMode.NONE) {
        (activity?.application as VadretApplication).cmp
    }

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View =
            inflater.inflate(layoutId(), container, false)

    override fun onDestroy() {
        subscriptions.clear()
        super.onDestroy()
    }
}