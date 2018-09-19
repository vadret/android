package fi.kroon.vadret.presentation

import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import fi.kroon.vadret.R
import fi.kroon.vadret.presentation.adapter.AboutAdapter
import fi.kroon.vadret.presentation.viewmodel.AboutViewModel
import fi.kroon.vadret.utils.extensions.toVisible
import fi.kroon.vadret.utils.extensions.viewModel
import kotlinx.android.synthetic.main.about_fragment.*
import javax.inject.Inject

class AboutFragment : BaseFragment() {

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
        aboutList.layoutManager = LinearLayoutManager(this.context, RecyclerView.VERTICAL, false)
        aboutList.adapter = aboutAdapter
    }

    override fun onDestroyView() {
        super.onDestroyView()
        aboutList.adapter = null
    }
}