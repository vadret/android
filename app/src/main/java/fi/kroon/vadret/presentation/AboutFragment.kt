package fi.kroon.vadret.presentation

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.tabs.TabLayout
import fi.kroon.vadret.R
import fi.kroon.vadret.presentation.adapter.ThirdPartyAdapter
import fi.kroon.vadret.presentation.adapter.BaseRowRecyclerAdapter
import fi.kroon.vadret.presentation.adapter.listener.AboutAdapterOnRowClickInterface
import fi.kroon.vadret.presentation.adapter.listener.BaseRowOnClickInterface
import fi.kroon.vadret.presentation.common.AbstractOnTabSelectedListener
import fi.kroon.vadret.presentation.common.model.BaseRowModel
import fi.kroon.vadret.presentation.dialog.ChangelogDialog
import fi.kroon.vadret.presentation.viewmodel.AboutViewModel
import fi.kroon.vadret.utils.extensions.toGone
import fi.kroon.vadret.utils.extensions.toVisible
import fi.kroon.vadret.utils.extensions.viewModel
import kotlinx.android.synthetic.main.about_app_tab_layout.*
import kotlinx.android.synthetic.main.about_fragment.*
import javax.inject.Inject

class AboutFragment : BaseFragment(), AboutAdapterOnRowClickInterface, BaseRowOnClickInterface {

    companion object {
        const val TAB_ABOUT_POSITION = 0
        const val TAB_LIBRARIES_POSITION = 1
    }

    override fun layoutId(): Int = R.layout.about_fragment

    @Inject
    lateinit var thirdPartyAdapter: ThirdPartyAdapter

    @Inject
    lateinit var infoAdapter: BaseRowRecyclerAdapter

    private lateinit var aboutViewModel: AboutViewModel

    private var changelogMessage: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        cmp.inject(this)
        aboutViewModel = viewModel(viewModelFactory) {}
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initialiseView()
        loadDependencies()
        observeViewModel()
    }

    private fun observeViewModel() {
        aboutViewModel.getChangelogText().observe({ this.lifecycle }, {
            changelogMessage = it
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        thirdPartyAdapter.unregisterListener()
        infoAdapter.unregisterListener()
        aboutList.adapter = null
        infoList.adapter = null
    }

    override fun onProjectClick(url: String) {
        openUrlInBrowser(url)
    }

    override fun onLicenseClick(url: String) {
        openUrlInBrowser(url)
    }

    override fun onSourceClick(url: String) {
        openUrlInBrowser(url)
    }

    override fun onBaseRowClick(baseRowModel: BaseRowModel) {
        if (baseRowModel.titleResId == R.string.changelog_row_title) {
            handleChangelogOnClick()
        } else if (baseRowModel.titleResId == R.string.souce_code_row_title) {
            handleSourceCodeRowOnClick(baseRowModel)
        } else if (baseRowModel.urlResId != null) {
            openUrlInBrowser(getString(baseRowModel.urlResId))
        }
    }

    private fun handleSourceCodeRowOnClick(baseRowModel: BaseRowModel) {
        baseRowModel.urlResId?.let {
            openUrlInBrowser(getString(it))
        }
    }

    private fun handleChangelogOnClick() {
        openChangelogDialog()
    }

    private fun openChangelogDialog() {
        ChangelogDialog()
            .setMessage(changelogMessage)
            .show(fragmentManager, ChangelogDialog.TAG)
    }

    private fun initialiseView() {
        setupAboutList()
        setupInfoList()
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

    private fun setupInfoList() {
        infoAdapter.setListener(this)
        infoList.layoutManager = LinearLayoutManager(this.context, RecyclerView.VERTICAL, false)
        infoList.adapter = infoAdapter
    }

    private fun setupAboutList() {
        thirdPartyAdapter.setListener(this)
        aboutList.layoutManager = LinearLayoutManager(this.context, RecyclerView.VERTICAL, false)
        aboutList.adapter = thirdPartyAdapter
    }

    private fun openUrlInBrowser(url: String) {
        val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        startActivity(browserIntent)
    }

    private fun loadDependencies() {
        aboutLayout.toVisible()
        thirdPartyAdapter.collection = aboutViewModel.getLibraries()
        infoAdapter.collection = aboutViewModel.getInfoRows()
    }
}