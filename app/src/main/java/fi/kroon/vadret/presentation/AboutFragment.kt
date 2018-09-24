package fi.kroon.vadret.presentation

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.tabs.TabLayout
import fi.kroon.vadret.R
import fi.kroon.vadret.presentation.adapter.AboutAdapter
import fi.kroon.vadret.presentation.adapter.AboutAdapterOnRowClickInterface
import fi.kroon.vadret.presentation.common.AbstractOnTabSelectedListener
import fi.kroon.vadret.presentation.viewmodel.AboutViewModel
import fi.kroon.vadret.utils.extensions.toGone
import fi.kroon.vadret.utils.extensions.toVisible
import fi.kroon.vadret.utils.extensions.viewModel
import kotlinx.android.synthetic.main.about_app_tab_layout.*
import kotlinx.android.synthetic.main.about_fragment.*
import javax.inject.Inject

class AboutFragment : BaseFragment(), AboutAdapterOnRowClickInterface {
    companion object {
        const val TAB_ABOUT_POSITION = 0
        const val TAB_LIBRARIES_POSITION = 1
    }

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

    private fun initialiseView() {
        setupAboutList()
        setupTabs()
    }

    private fun setupTabs() {
        tabLayout.addTab(tabLayout.newTab().setText(getString(R.string.about_app_tab_title)))
        tabLayout.addTab(tabLayout.newTab().setText(getString(R.string.libraries_tab_title)))
        tabLayout.addOnTabSelectedListener(object : AbstractOnTabSelectedListener() {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                when (tab?.position) {
                    TAB_ABOUT_POSITION -> handleAboutTabSelected()
                    TAB_LIBRARIES_POSITION -> handleLibrariesTabSelected()
                }
            }
        })
    }

    private fun handleLibrariesTabSelected() {
        aboutList.toVisible()
        aboutLayout.toGone()
    }

    private fun handleAboutTabSelected() {
        aboutList.toGone()
        aboutLayout.toVisible()
    }

    private fun setupAboutList() {
        aboutAdapter.registerListener(this)
        aboutList.layoutManager = LinearLayoutManager(this.context, RecyclerView.VERTICAL, false)
        aboutList.adapter = aboutAdapter
    }

    private fun openUrlInBrowser(url: String) {
        val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        startActivity(browserIntent)
    }

    private fun loadDependencies() {
        aboutLayout.toVisible()
        aboutAdapter.collection = aboutViewModel.get()
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