package fi.kroon.vadret.presentation

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import fi.kroon.vadret.R
import fi.kroon.vadret.presentation.adapter.AboutAdapter
import fi.kroon.vadret.presentation.adapter.AboutAdapterOnRowClickInterface
import fi.kroon.vadret.presentation.viewmodel.AboutViewModel
import fi.kroon.vadret.utils.extensions.toVisible
import fi.kroon.vadret.utils.extensions.viewModel
import kotlinx.android.synthetic.main.about_fragment.*
import javax.inject.Inject

class AboutFragment : BaseFragment(), AboutAdapterOnRowClickInterface {
    /**
     *  ThirdParty page for external libraries
     */

    override fun layoutId(): Int = R.layout.about_fragment

    @Inject
    lateinit var aboutAdapter: AboutAdapter

    private lateinit var aboutViewModel: AboutViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        cmp.inject(this)
        aboutViewModel = viewModel(viewModelFactory) {}
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initialiseView()
        loadDependencies()
    }

    private fun loadDependencies() {
        aboutList.toVisible()
        aboutAdapter.collection = aboutViewModel.get()
    }

    private fun initialiseView() {
        aboutAdapter.registerListener(this)
        aboutList.layoutManager = LinearLayoutManager(this.context, RecyclerView.VERTICAL, false)
        aboutList.adapter = aboutAdapter
    }

    private fun openUrlInBrowser(url: String) {
        val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        startActivity(browserIntent)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        aboutAdapter.unregisterListener()
        aboutList.adapter = null
    }

    override fun onProjectClick(url: String) {
        openUrlInBrowser(url)
    }

    override fun onLicenceClick(url: String) {
        openUrlInBrowser(url)
    }

    override fun onSourceClick(url: String) {
        openUrlInBrowser(url)
    }
}