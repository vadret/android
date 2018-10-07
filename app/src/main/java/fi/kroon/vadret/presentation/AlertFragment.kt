package fi.kroon.vadret.presentation

import android.os.Bundle
import android.view.View
import androidx.annotation.StringRes
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import fi.kroon.vadret.R
import fi.kroon.vadret.data.alert.exception.AlertFailure
import fi.kroon.vadret.data.alert.model.Alert
import fi.kroon.vadret.data.alert.model.Warning
import fi.kroon.vadret.data.exception.Either
import fi.kroon.vadret.data.exception.Failure
import fi.kroon.vadret.presentation.adapter.AlertAdapter
import fi.kroon.vadret.presentation.viewmodel.AlertViewModel
import fi.kroon.vadret.utils.Schedulers
import fi.kroon.vadret.utils.extensions.toVisible
import fi.kroon.vadret.utils.extensions.viewModel
import io.reactivex.rxkotlin.addTo
import kotlinx.android.synthetic.main.alert_fragment.*
import timber.log.Timber
import javax.inject.Inject

class AlertFragment : BaseFragment() {

    override fun layoutId() = R.layout.alert_fragment

    @Inject
    lateinit var schedulers: Schedulers

    @Inject
    lateinit var alertAdapter: AlertAdapter

    private lateinit var alertViewModel: AlertViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        cmp.inject(this)
        alertViewModel = viewModel(viewModelFactory) {}
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initialiseView()
        loadAlerts()
    }

    private fun initialiseView() {
        alertRv.layoutManager = LinearLayoutManager(this.context, RecyclerView.VERTICAL, false)
        alertRv.adapter = alertAdapter
        alertRv.hasFixedSize()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        alertRv.adapter = null
    }

    private fun loadAlerts() {
        alertRv.toVisible()
        alertViewModel
            .get()
            .subscribeOn(schedulers.io())
            .observeOn(schedulers.ui())
            .onErrorReturn { Either.Left(Failure.IOException()) }
            .subscribe(::alertHandler)
            .addTo(subscriptions)
    }

    private fun alertHandler(data: Either<Failure, Alert>) {
        data.either(::handleFailure, ::handleAlert)
    }

    private fun handleAlert(alert: Alert) {
        Timber.d("Data: $alert")
        renderAlert(warningList = alert.alert)
    }

    private fun renderAlert(warningList: List<Warning>) {
        alertAdapter.collection = warningList
    }

    private fun handleFailure(failure: Failure?) {
        when (failure) {
            is AlertFailure.NoAlertAvailable -> renderFailure(R.string.no_alert_available)
            is Failure.IOException -> renderFailure(R.string.io_exception)
            is Failure.NetworkException -> renderFailure(R.string.network_failure)
            is Failure.NetworkOfflineFailure -> renderFailure(R.string.no_network_available)
        }
    }

    private fun renderFailure(@StringRes message: Int) {
        return Snackbar.make(this.view!!, message, Snackbar.LENGTH_LONG).show()
    }
}