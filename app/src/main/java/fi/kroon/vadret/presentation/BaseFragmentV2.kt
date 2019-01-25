package fi.kroon.vadret.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import io.reactivex.disposables.CompositeDisposable
import javax.inject.Inject

abstract class BaseFragmentV2 : Fragment() {

    abstract fun layoutId(): Int

    /**
     *  TODO refactor into not being a shared compositeDisposable
     *  could potentially cause problöems if several callers use
     *  the same subscription instance and one decide to kill it --
     *  which might happen if they are within the same scope.
     */

    @Inject
    protected lateinit var subscriptions: CompositeDisposable

    abstract fun renderError(errorCode: Int)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View =
        inflater.inflate(layoutId(), container, false)

    override fun onDestroy() {
        subscriptions.clear()
        super.onDestroy()
    }
}